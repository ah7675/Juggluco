/*      This file is part of Juggluco, an Android app to receive and display         */
/*      glucose values from Freestyle Libre 2 and 3 sensors.                         */
/*                                                                                   */
/*      Copyright (C) 2021 Jaap Korthals Altes <jaapkorthalsaltes@gmail.com>         */
/*                                                                                   */
/*      Juggluco is free software: you can redistribute it and/or modify             */
/*      it under the terms of the GNU General Public License as published            */
/*      by the Free Software Foundation, either version 3 of the License, or         */
/*      (at your option) any later version.                                          */
/*                                                                                   */
/*      Juggluco is distributed in the hope that it will be useful, but              */
/*      WITHOUT ANY WARRANTY; without even the implied warranty of                   */
/*      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.                         */
/*      See the GNU General Public License for more details.                         */
/*                                                                                   */
/*      You should have received a copy of the GNU General Public License            */
/*      along with Juggluco. If not, see <https://www.gnu.org/licenses/>.            */
/*                                                                                   */
/*      Fri Jan 27 15:25:03 CET 2023                                                 */


#ifndef INOUT_H
#define INOUT_H
#include <string>
#include <string_view>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <sys/mman.h>
#include <errno.h>
#include <math.h>
#include <iomanip>
    #include <stdarg.h>

#include <unistd.h>
#include "logs.hpp"
//#include <span>
#ifndef LOGGER
//#define LOGGER(...)  { fprintf(stderr,__VA_ARGS__); fflush(stderr); }
inline  int LOGGER( const char* fmt, ...) {
        va_list args;
        va_start(args, fmt);
	int res=	vfprintf(stderr, fmt, args);
        va_end(args);
	return res;
	}
#endif

#include "sizear.hpp"
using namespace std;
class pathconcat {
char *name;
int namelen;
public:
template<typename T,typename ...Ts> char *	nconcat(int len,  T &&one,  Ts &&... args) {
	char *ptr;
	int en=sizear(one);

	const char *asstr=reinterpret_cast<const char *>( &one[0]);
	if constexpr(sizeof...(Ts)==0) {
		namelen=len+en+1;
		ptr= new char[namelen];
		ptr[namelen-1]='\0';
		}
	else {
		do {
			en--;
			} while(en>=0&&(asstr[en]=='/'||asstr[en]=='\0'));
		en++;
		ptr=nconcat(len+en+1,args...);
		ptr[len+en]='/';
		}
	memcpy(ptr+len,asstr,en);
	return ptr;
	}

template <typename ...Ts>
pathconcat(  Ts &&... args) {
	name=nconcat(0,args ...);
	}
pathconcat( pathconcat &&in):name(in.name),namelen(in.namelen) { 
	in.name=nullptr;
	in.namelen=0;
	fprintf(stderr,"pathconcat( pathconcat &&in=%s)\n",name);
}
pathconcat( pathconcat &in):name(new char[in.namelen]),namelen(in.namelen) { 
	memcpy(name,in.name,in.namelen);
	fprintf(stderr,"pathconcat( pathconcat &in=%s)\n",name);
}
pathconcat &operator=(pathconcat &&in) {
	fprintf(stderr,"pathconcat &operator=(pathconcat &&in %s) {\n",in.name);
	std::swap(name,in.name);
	std::swap(namelen,in.namelen);
	return *this;
	}
pathconcat &operator=(pathconcat &in) {
	fprintf(stderr,"pathconcat &operator=(pathconcat &in %s) {\n",in.name);
	delete[] name;
	name=new char[in.namelen];
	namelen=in.namelen;
	memcpy(name,in.name,namelen);
	return *this;
	}
~pathconcat() {
	delete[] name;
	}
operator const string_view() const {
	return string_view(name,namelen-1);
	}
char *begin() {return data();}
char *end() {return begin()+size();}
const char *cbegin() const {return data();}
const char *cend() const {return begin()+size();}
const char *begin() const {return cbegin();}
const char *end() const {return cend();}
char * data() { return name;}
const char * data() const { return name;}
const char *c_str() const {
	return data();
	}
char *c_str()  {
	return data();
	}
operator char *() {
	return name;
	}
operator const char *() const {
	return name;
	}
int size() const {return namelen-1;}
int length() const {return size();}
};


class Open {
int fp;
public:
Open(const char *name,int flags,int mode): fp{open(name,flags,mode)} {
       }
Open(const char *name,int flags): fp{open(name,flags)} {
       }
virtual ~Open() {
  close(fp);
  }
operator int() {return fp;}
 };
class Create:public Open {
public:
Create(const char *name): Open(name,O_RDWR|O_CREAT|O_TRUNC ,S_IRUSR |S_IWUSR) {
       }
 };

inline bool writeall(const char *name,const void *data,const int len) {
      Create file(name);
      if(file==-1||write(file,data,len)!=len) {
      	lerror(name);
      	return false;
	}
	return true;
     }
inline int readfile(const char *name,void *data,int len) {
	Open file(name,O_RDONLY);
	if(file<0)
		return -2;
	return read(file,data,len);
	}
template<class T>
inline T readtype(const char *name) {
	T out;
	readfile(name,&out,sizeof(out));
	return out;
	}
   
template <typename T=char>
class Readall {
size_t len;
T *all;
public:
Readall():len(0),all(nullptr){}
Readall(const char *data,size_t lenin):len(lenin/sizeof(T)), all(new T[len+1]){
	memcpy(all,data,sizeof(T)*len);
	all[len]='\0';
	}
template <typename T1=T>
Readall(const T1 *data,size_t len):len(len),all(new T1[len]) {
	memcpy(all,data,sizeof(T1)*len);
	}

T* fromfile(const char *name) {
	struct stat st;
	if(stat(name,&st)==-1) {
		all=NULL;
		return nullptr;
		}
	len=(st.st_size)/sizeof(T);
	if	constexpr (sizeof(T)==1) {
		all=new T[len+1];
		all[len]='\0';
		}
	else
		all=new T[len+1];
	if(readfile(name,all,st.st_size)!=st.st_size) {
		delete[] all;
		all=nullptr;
		return nullptr;
		}
	return all;
	}
Readall(const char *name) {
	fromfile(name);
	}
Readall(Readall &&in):all(in.all),len(in.len) {
//	std::cerr<<"Readall(&&) "<<all<<std::endl;
	in.all=nullptr;
	}
	
//Readall(string_view base,string_view endname): Readall(pathconcat(base,endname)) {}
~Readall() {
//	std::cerr<<"~Readall() "<<std::endl;
	delete[] all;
	}
int length()const { return len;}
int size()const { return length();}
T * data()  { return all;}
const T * data()const { return all;}
const  T *c_str() const {
	return data();
	}
const T *begin() {return data();}
const T *end() {return data()+length();}
operator  T *() {return data();}
operator  const T *() const  {return data();}
operator  basic_string_view<T>() const { return basic_string_view<T>{all,len};}

void shrink(size_t siz) {
	len=siz;
	}
//operator  span<char>() { return span<char>{all,len};}
};

template <class T> class Mmap {
protected:
size_t  len;
void *buf;


void *mopen(const char *filename) {
	if(!filename)
		return nullptr;
      int    fp= open(filename,O_RDWR|O_CREAT,S_IRUSR |S_IWUSR);
        if(fp==-1) {
               // cout<<"open "<<name<<" failed"<<std::endl;
	       lerror(filename);
                return nullptr;
                }
	 struct stat st;
        if(fstat(fp,&st)!=0) {
	       lerror(filename);
                return nullptr;
                }
	if(len && st.st_size<len) {
		if(ftruncate(fp,len)) {
			lerror("ftruncate");
			close(fp);
			return nullptr;
			}
		}
	else
		len=st.st_size;
        void *mmapbuf=mmap(NULL, len, PROT_READ |PROT_WRITE,MAP_SHARED, fp, 0);
       close(fp);
       if(mmapbuf== MAP_FAILED) {
       		lerror("mmap");
                return nullptr;
		}
        return mmapbuf;
        }

void *mopen(const string_view base,const string_view endname) {
      return mopen(pathconcat(base,endname));
      }
public:
Mmap(Mmap && ander):len(ander.len),buf(ander.buf) {
//	LOGGER(" Mmap(Mmap && ander)\n");
	ander.buf=nullptr;
	}
Mmap& operator=(Mmap&& ander) {
//	LOGGER("Mmap& operator=(Mmap&& ander) {");
	this->~Mmap() ;
	buf=ander.buf;
	len=ander.len;
	ander.buf=nullptr;
	return *this;
	}
Mmap(size_t count):len(count*sizeof(T)),buf(mmap(NULL,len, PROT_READ |PROT_WRITE,MAP_ANONYMOUS|MAP_PRIVATE,-1,0)) {
//	LOGGER(" Mmap(size_t)\n");
if(MAP_FAILED==buf)
	buf=nullptr;

}
Mmap(const char *filename,size_t  count=0):len(count*sizeof(T)),buf(mopen(filename)) {
//	LOGGER("Mmap %s %p %zu\n",filename,buf,len);
}
Mmap(const string_view base,const string_view endname,int count=0):Mmap(pathconcat(base,endname),count) { 
//LOGGER("MMap string_view %s\n",endname.data());

}

T* data() {
	return (T*) buf;
}
const T* data() const {
	return (const T*) buf;
}
operator T*() {
	return data();
	}
operator const T*() const {
	return data();
	}
/*	
T &operator [](int ind) {
	return ((T *)buf)[ind];
	}
T & operator[](int index) 
	{ return this()[index];};
	*/

auto count() const {

	return len/sizeof(T);
	}
auto size() const {
	return count();
	}
/*operator T&() {
	return *((T*) buf);
	}

bool extend(int make) {
	auto cur=count();
	if(cur<make) {
		auto extra=make*sizeof(T)-len;
		void *end=(char *)buf+len;
		void *news=mmap(end, extra, PROT_READ |PROT_WRITE,MAP_ANONYMOUS|MAP_SHARED, -1, 0);
		if(news==end) {
			len+=extra;	
			return true;
			}
		else {
			lerror("mmap");
			LOGGER("mmap failed returning %p\n",news);
			}
		}
	return false;
	}
*/	
template <typename... Ts>
void extend(Ts... args) {
	this->~Mmap();
	new(this) Mmap(args...);
	}



virtual ~Mmap() {
//	LOGGER("munmap %p %zu\n",buf,len);
	if(buf&&buf!=MAP_FAILED)
		munmap(buf,len);
	}
        
};

template <typename T>
struct gegs{
typedef T type;
int len;
T buf[];
int length() const { return len; };
int size() const { return length(); }
T * data() { return buf; };
const T * data() const { return buf; };
static gegs *readfile(const char name[]) ;
//static gegs * newex(int len) ;
static gegs * newex(int len) {
		gegs *ar= reinterpret_cast<gegs *>(new(std::align_val_t(alignof(struct gegs)))  unsigned char[sizeof(gegs)+len*sizeof(T)]);
		ar->len=len;
		return ar;
		}

template <typename Typ,  std::size_t  N> 
static gegs * newex( const std::array<Typ,N> &ar) {
	gegs<T> *ptr=gegs<T>::newex(N);
	memcpy(ptr->buf,ar.data(),N*sizeof(T));
	return ptr;
	}
static gegs * newex( const gegs *in) {
	gegs *ptr=newex(in->length());
	memcpy(ptr->buf,in->buf,in->length()*sizeof(T));
	return ptr;
	}
static void deleteex(gegs *geg) {
	operator delete[] (reinterpret_cast<unsigned char *>(geg), std::align_val_t(alignof(struct gegs)));
	}
};
//typedef gegs<unsigned char> data_t;

template<typename T> gegs<T> *gegs<T>::readfile(const char name[]) {
	struct stat st;
	if(stat(name,&st)==-1) {
		return nullptr;
		}
	gegs *ar= newex((st.st_size+sizeof(T)-1)/sizeof(T));
	if(::readfile(name,ar->buf,st.st_size) )
		return ar;
	delete[] reinterpret_cast<unsigned char *>(ar);
	return nullptr;
	}
typedef gegs<signed char> data_t;
#endif
