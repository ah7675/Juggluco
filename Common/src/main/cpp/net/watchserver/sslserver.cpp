
#ifndef WEAROS
char fullchainfileonly[]="fullchain.pem";
char privatekey[]="privkey.pem";
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
/*      Fri Jan 27 12:38:28 CET 2023                                                 */


#ifdef USE_SSL
#include <stdlib.h>
#include <dlfcn.h>
#ifdef JUGGLUCO_APP
#include <android/dlext.h>
#endif
#include <unistd.h>

#include <string>
#include <string_view>
#ifndef HAVE_NOPRCTL
#include <sys/prctl.h>
#endif
#include "strconcat.hpp"
#include "openssl/ssl.h"
#include "openssl/err.h"
#include "logs.hpp"
#include "inout.hpp"
#include "watchserver.hpp"
#include "destruct.hpp"
const SSL_METHOD *(*TheMethod)(void);
int (*SSL_library_initptr)(void)=NULL;
void (*OPENSSL_add_all_algorithms_noconfptr)(void)=NULL;

void (*SSL_load_error_stringsptr)(void)=NULL;

extern int logcallback(const char *str, size_t len, void *u) ;

SSL_CTX *(*SSL_CTX_newptr)(const SSL_METHOD *method);

extern void (*ERR_print_errors_cbptr)(int (*cb)(const char *str, size_t len, void *u), void *u);
int (*SSL_CTX_use_certificate_chain_fileptr)(SSL_CTX *ctx, const char *file);

//int (*SSL_CTX_use_certificate_ASN1ptr)(SSL_CTX *ctx, int len, unsigned char *d);
//int (*SSL_CTX_use_PrivateKey_ASN1ptr)(int pk, SSL_CTX *ctx, unsigned char *d, long len);
int (*SSL_CTX_use_PrivateKey_fileptr)(SSL_CTX *ctx, const char *file, int type);
//const char* (*ERR_reason_error_stringptr)(unsigned long e);
//unsigned long (*ERR_get_errorptr)(void);
//unsigned long (*ERR_peek_last_errorptr)(void);


int (*SSL_CTX_check_private_keyptr)(const SSL_CTX *ctx);
int (*SSL_acceptptr)(SSL *ssl);
int (*SSL_readptr)(SSL *ssl, void *buf, int num);
int (*SSL_writeptr)(SSL *ssl, const void *buf, int num);
int (*SSL_get_fdptr)(const SSL *ssl);
void (*SSL_freeptr)(SSL *ssl);
SSL *(*SSL_newptr)(SSL_CTX *ctx);
int (*SSL_set_fdptr)(SSL *ssl, int fd);
void (*SSL_CTX_freeptr)(SSL_CTX *ctx);

void  sslerror(const char *format) {
	ERR_print_errors_cbptr(logcallback,(void*)format);
	}


extern std::string_view globalbasedir;




static pathconcat chainfilename;
static pathconcat private_file;
static bool getkeynames() {
	 chainfilename=pathconcat(globalbasedir,fullchainfileonly);
	 private_file=pathconcat(globalbasedir,privatekey);
	 LOGAR("getkeynames");
	 return true;
	}
std::string haskeyfiles() {
[[maybe_unused]] static auto _hasnames=getkeynames();
 if(access(chainfilename.data(), R_OK)!=0) {
	return std::string(fullchainfileonly)+std::string(" missing");
	}
 if(access(private_file.data(), R_OK)!=0) {
	return std::string(privatekey)+std::string(" missing");

	}
	return "";
	}

extern void *opencrypto();
extern void *openssl();
extern void * dlopener(std::string_view filename,int flags);
std::string loadsslfunctions() {
   #ifndef  JUGGLUCO_APP
   char cryptolib[]="libcrypto.so.3";
   void* cryptohandle;
   if(!(cryptohandle=dlopener(cryptolib, RTLD_NOW))&&(cryptolib[12]='\0', !(cryptohandle=dlopener(cryptolib, RTLD_NOW)))) {
         cryptolib[12]='.';
        return  std::string("dlopen==nullptr: ")+std::string(dlerror());
        }
   #else
   void* cryptohandle=opencrypto();
   if(!cryptohandle) {
        return  std::string("dlopen==nullptr: ")+std::string(dlerror());
        }
   #endif
   #define hgetsym(handle,name) *((void **)&name##ptr)=dlsym(handle, #name)
   #define getsym(name) hgetsym(handle,name)
   #define symtest(name) if(!(getsym(name))) { dlclose(handle);dlclose(cryptohandle);return std::string(dlerror());;}
   if(!(hgetsym(cryptohandle,ERR_print_errors_cb))) {
        dlclose(cryptohandle);
        return std::string("hgetsym ERR_print_errors_cb fails");
      }

#ifndef JUGGLUCO_APP
   char libssl[]="libssl.so.3";
   const char *libname=libssl;
     void *handle;
     if(!(handle=dlopener(libname, RTLD_NOW))&&(libssl[9]='\0',!(handle=dlopener(libname, RTLD_NOW)))) {
         libssl[9]='.';
        return std::string("dlopen==nullptr: ")+std::string(dlerror());
        }
#else
     void *handle=openssl();
     if(!handle) {
        return std::string("dlopen==nullptr: ")+std::string(dlerror());
        }
#endif
     *((void **)&TheMethod)=dlsym(handle, "TLSv1_2_server_method");
     if(!TheMethod) {
        LOGGER("dlsym(TLSv1_2_server_method): %s\n",dlerror());
      *((void **)&TheMethod)=dlsym(handle, "SSLv23_method");
         if(!TheMethod) {
            dlclose(handle);
            return std::string("dlsym(SSLv23_method): ")+std::string(dlerror());
            }
      }

   getsym(SSL_library_init);
   getsym(OPENSSL_add_all_algorithms_noconf);
   getsym(SSL_load_error_strings);
   symtest(SSL_CTX_new);
   symtest(SSL_CTX_use_certificate_chain_file);
   symtest(SSL_CTX_use_PrivateKey_file);
   symtest(SSL_CTX_check_private_key);
   symtest(SSL_accept);
   symtest(SSL_read);
   symtest(SSL_write);
   symtest(SSL_get_fd);
   symtest(SSL_free);
   symtest(SSL_new);
   symtest(SSL_set_fd);
   symtest(SSL_CTX_free);
   return "";
 }



extern std::string_view servererrorstr;

void	sslservererror(SSL *ssl) {
	SSL_writeptr(ssl,servererrorstr.data(),servererrorstr.size()) ;
	}
extern bool sslstopconnection;
bool sslstopconnection=false;
bool securewatchcommands(SSL *ssl) {
	constexpr const int RBUFSIZE=4096;
	char rbuf[RBUFSIZE];
	int len;
	if((len= SSL_readptr(ssl,rbuf,RBUFSIZE))<=0) {
		sslservererror(ssl);
		return false;
		}
	LOGGER("securewatchcommands %d\n",len);
	struct recdata outdata;
	if(sslstopconnection)
		return false;

	     void wakesender();
	      wakesender();
bool watchcommands(char *rbuf,int len,recdata *outdata,bool secure) ;
/*
	if(!watchcommands(rbuf, len,&outdata,true) ) {
		sslservererror(ssl);
		return false;
		}
	return SSL_writeptr(ssl,outdata.data(),outdata.size())&&!sslstopconnection;
; */
	bool res=watchcommands(rbuf, len,&outdata,true);
	bool res2= SSL_writeptr(ssl,outdata.data(),outdata.size());
	return res&&res2&&!sslstopconnection;
	} 

static SSL_CTX *globalctx=nullptr;

bool	securewatchcommands(SSL *ssl);
extern void sendtimeout(int sock,int secs);
extern void receivetimeout(int sock,int secs) ;

void handlewatchsecure(int sock) {
   destruct _des([sock]{close(sock);});
	static SSL_CTX *ctx=globalctx;
	if(!ctx)
		return;
   const char threadname[17]="ssl watchconnect";
#ifndef HAVE_NOPRCTL
   prctl(PR_SET_NAME, threadname, 0, 0, 0);
#endif
   LOGGER("handlewatchsecure %d\n",sock);
   SSL *ssl=SSL_newptr(ctx);  
	if(!ssl)
		return;
 	receivetimeout(sock,60);
 	sendtimeout(sock,5*60);
   SSL_set_fdptr(ssl, sock); 
   if(SSL_acceptptr(ssl)<0)   { 
      sslerror("SSL_accept: %s");
    	SSL_freeptr(ssl);  
      return;
      }
	securewatchcommands(ssl);
   SSL_freeptr(ssl);  
	}

 #include <openssl/err.h>


struct call_data_t{
	std::string_view start;
	std::string back;
	} ;
int geterrorcallback(const char *str, size_t len, void *u) {
	call_data_t *dptr=(call_data_t *)u;
	int startlen =dptr->start.size();
	int totlen=startlen+len+3;
	dptr->back=std::string(totlen,0);
	char *uit=dptr->back.data();
	memcpy(uit,dptr->start.data(),startlen);
	uit+=startlen;
	*uit++=':';
	*uit++=' ';
	memcpy(uit,str,len);
	uit[len]='\0';
	return 0;
	}
std::string geterror(std::string_view start) {
	call_data_t data;
	data.start=start;
	ERR_print_errors_cbptr(geterrorcallback,(void*)&data);
	return data.back;
	}
//static SSL_CTX* 
/*
const char *geterror() {
	if(ERR_reason_error_stringptr)
		 return ERR_reason_error_stringptr(ERR_peek_last_errorptr());
	else return "openSSL error";
	} */

const std::string initsslserver(void) {
    LOGSTRING("initsslserver\n");
	if(globalctx!=nullptr)
		SSL_CTX_freeptr(globalctx);
	globalctx=nullptr;
 class init{
	 public:
		init() {
		    if(SSL_library_initptr) SSL_library_initptr();
		    if(OPENSSL_add_all_algorithms_noconfptr) OPENSSL_add_all_algorithms_noconfptr();
		    if(SSL_load_error_stringsptr) SSL_load_error_stringsptr();  
		    } 
	    };
 static init _init;

    SSL_CTX *ctx = SSL_CTX_newptr(TheMethod());
    if( ctx == NULL ) {
		return geterror("SSL_CTX_newptr");
		}

    destruct des{[ctx] {SSL_CTX_freeptr(ctx);  }};
    
if(SSL_CTX_use_certificate_chain_fileptr(ctx,chainfilename.data())<=0)  {
	return geterror(chainfilename);
	}
if(SSL_CTX_use_PrivateKey_fileptr(ctx, private_file.data(), SSL_FILETYPE_PEM) <= 0) {
	return geterror(private_file.data());
    }
    if( !SSL_CTX_check_private_keyptr(ctx) ) {
	return geterror("Private key does not match the public certificate");
    }
    des.active=false;
    globalctx=ctx;
	return "";
   }



/*
extern void linkopenssl();
void linkopenssl() {
//const char *filename="/apex/com.android.conscrypt/lib64/libssl.so";
const char *filename="/system/lib64/libssl.so";
void * handle;
   if((handle=bypass_loader_dlopen(filename,RTLD_LAZY|RTLD_GLOBAL))) {
     LOGGER("bypass_loader_dlopen %s succeeded\n",filename);
  }
else {
      LOGGER("dlopen %s failed\n",filename);
      return;
    }
    const char *symbol;
   SSL_CTX *(*SSL_CTX_newptr)(const SSL_METHOD *method);
    symbol= "SSL_CTX_new";
   if( (*(void **) (&SSL_CTX_newptr)=dlsym(handle, symbol))) {
      LOGGER("dlsym %s success!\n",symbol);
      }
   else {
      LOGGER("dlsym %s failed: %s\n",symbol,dlerror());
      }

//   dlclose(handle);
}
*/
#endif
#endif
