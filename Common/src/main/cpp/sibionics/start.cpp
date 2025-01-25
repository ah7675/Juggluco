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
/*      Thu Apr 04 20:14:31 CEST 2024                                                 */


#ifdef SIBIONICS

#include "config.h"
//#define SI3ONLY
//#define SIHISTORY
#include <dlfcn.h>
#include <string_view>
#include <mutex>
#include "SensorGlucoseData.hpp"
#include "streamdata.hpp" 
#include "fromjava.h"
#include "destruct.hpp"
#include "sibionics/AlgorithmContext.hpp"
#include "datbackup.hpp"
extern void	sendstreaming(SensorGlucoseData *hist);

extern bool siInit();

extern "C" JNIEXPORT jstring JNICALL   fromjava(getSiBluetoothNum)(JNIEnv *envin, jclass cl,jlong dataptr) {
	if(!dataptr)
		return nullptr;
	const SensorGlucoseData *usedhist=reinterpret_cast<streamdata *>(dataptr)->hist ; 
	if(!usedhist)
		return nullptr;
	if(!usedhist->isSibionics())
		return nullptr;
	const char *name=usedhist->getinfo()->siBlueToothNum;
	LOGGER("getSiBluetoothNum()=%s\n",name);
	return envin->NewStringUTF(name);
	} 

extern "C" JNIEXPORT jstring JNICALL   fromjava(addSIscangetName)(JNIEnv *env, jclass cl,jstring jgegs) {
   const char *gegs = env->GetStringUTFChars( jgegs, NULL);
   if(!gegs) return 0LL;
   destruct   dest([jgegs,gegs,env]() {env->ReleaseStringUTFChars(jgegs, gegs);});
   const size_t gegslen= env->GetStringUTFLength( jgegs);
   auto [sensindex,sens]= sensors->makeSIsensorindex({gegs,gegslen},time(nullptr));
   if(sens) {
      const char *name=sens->shortsensorname()->data();
      LOGGER("addSIscangetName(%s)=%s\n",gegs,name);
      sendstreaming(sens);  //TODO??
      backup->resendResetDevices();
      backup->wakebackup(Backup::wakeall);
      return env->NewStringUTF(name);
      }
   return nullptr;
   }
extern "C" JNIEXPORT void JNICALL   fromjava(siSaveDeviceName)(JNIEnv *env, jclass cl,jlong dataptr,jstring jdeviceName) {
	if(!dataptr)
		return;
	streamdata *sdata=reinterpret_cast<streamdata *>(dataptr);
	jint getlen= env->GetStringUTFLength( jdeviceName);
    auto *sens= sdata->hist;
   auto *info=sens->getinfo();
   const int maxlen=sizeof(info->siDeviceName);
	if((getlen+1)>maxlen) {
		LOGGER("deviceNamelen=%d toolarge\n",getlen);
		}	
   int len=std::min(maxlen-1,getlen);
   info->siToken='%';
   char *name=(char *)info->siDeviceName;
   env->GetStringUTFRegion( jdeviceName, 0,len, name);
   info->siDeviceNamelen=len;
   name[len]='\0';
  // sendstreaming(sens);  //TODO??
   }

/*
extern "C" JNIEXPORT int JNICALL   fromjava(getSIindex)(JNIEnv *env, jclass cl,jlong dataptr) {
	if(!dataptr)
		return 0;
	const streamdata *sdata=reinterpret_cast<const streamdata *>(dataptr);
	return sdata->hist->getSiIndex();
   } */

extern bool savejson(SensorGlucoseData *sens,std::string_view, int index,const AlgorithmContext *alg );

extern data_t *fromjbyteArray(JNIEnv *env,jbyteArray jar,jint len=-1);

extern "C" JNIEXPORT void JNICALL   fromjava(EverSenseClear)(JNIEnv *env, jclass cl,jlong dataptr) {
 	reinterpret_cast<streamdata *>(dataptr)->hist->setbroadcastfrom(INT_MAX);
	}
extern "C" JNIEXPORT jlong JNICALL   fromjava(SIprocessData)(JNIEnv *envin, jclass cl,jlong dataptr, jbyteArray bluetoothdata,jlong mmsec) {
if(!dataptr) {
   LOGAR("SIprocessData dataptr==null");
   return 0LL;
    }
  uint32_t timsec=mmsec/1000L;
 data_t *bluedata=fromjbyteArray(envin,bluetoothdata);
  destruct _destbluedata([bluedata]{data_t::deleteex(bluedata);});
 sistream *sdata=reinterpret_cast<sistream *>(dataptr);
  SensorGlucoseData *sens=sdata->hist;
  if(!sens) {
      LOGAR("SIprocessData SensorGlucoseData==null");
      return 0LL;
     }
#ifdef NOTCHINESE
   if(sens->notchinese()) {
	 return sdata->sicontext.processData2(sens,timsec,bluedata,sdata->sensorindex);
   	}
   else   
#endif
   {
	 return sdata->sicontext.processData(sens,timsec,bluedata->data(),bluedata->size(),sdata->sensorindex);
	 }
}
extern std::string_view libdirname;
#include "sibionics/json.hpp"
void *openlib(std::string_view libname) {
	int liblen=libdirname.size();
   if(liblen<=0) {
      LOGGER("libdirname.size()=%d\n",liblen);
      return  nullptr;
      }
	int libnamelen=libname.size()+1;
	char fullpath[libnamelen+ liblen];
	memcpy(fullpath,libdirname.data(),liblen);
	memcpy(fullpath+liblen,libname.data(),libnamelen);
	LOGGER("open %s\n",fullpath);
	return dlopen(fullpath, RTLD_NOW);
	}



//"_ZN21NativeAlgorithmV1_1_223getJsonAlgorithmContextEv";
//_ZN22NativeAlgorithmV1_1_3B23getJsonAlgorithmContextEv
//_ZN22NativeAlgorithmV1_1_3B23setJsonAlgorithmContextEPc
//#define algjavastr(x) "Java_com_algorithm_v1_11_13_1b_NativeAlgorithmLibraryV1_11_13B_" #x
#include "jnidef.h"

extern JNIEnv *subenv;

extern JavaVM *getnewvm();

extern bool loadjson(SensorGlucoseData *sens, const char *statename,const AlgorithmContext *alg, setjson_t setjson) ;
#ifdef SI3ONLY
#undef algLibName 
#undef jniAlglib 
#undef vers
#undef algjavastr
#undef jsonname

#define jsonname(et,end) "_ZN22NativeAlgorithmV1_1_3B23" #et  "JsonAlgorithmContext" #end //Makes one in 5 minutes
#define algLibName "/libnative-algorithm-v1_1_3_B.so";
#define jniAlglib 	"/libnative-algorithm-jni-v113B.so";
#define vers(x) x 
#undef algjavastr


#undef targetlow 
#undef targethigh
#define targetlow 3.9
#define targethigh 7.8
#define algjavastr(x) "Java_com_algorithm_v1_11_13_1b_NativeAlgorithmLibraryV1_11_13B_" #x


#include "jnifuncs.hpp"
#else
#undef jniAlglib 
#undef vers
#undef algjavastr

#undef targetlow 
#undef targethigh
#ifdef NOTCHINESE
#define targetlow 4.4
#define targethigh 11.1
//#define jniAlglib 	"/libnative-algorithm-jni-v112.so";

#define jniAlglib 	"/libnative-algorithm-jni-v112F.so";
//#define algjavastr(x) "Java_com_algorithm_v1_11_12_NativeAlgorithmLibraryV1_11_12_" #x
#define algjavastr(x) "Java_com_algorithm_v1_11_12_1f_NativeAlgorithmLibraryV1_11_12F_" #x

//#define algLibName "/libnative-algorithm-v1_1_2.so"
#define algLibName "/libnative-algorithm-v1_1_2F.so";

//#define jsonname(et,end) "_ZN21NativeAlgorithmV1_1_223" #et  "JsonAlgorithmContext" #end

#define jsonname(et,end) "_ZN22NativeAlgorithmV1_1_2F23" #et  "JsonAlgorithmContext" #end
#define vers(x) x ## 2

#include "jnifuncs.hpp"
#endif

//#ifdef SIHISTORY
#if 1
#undef algLibName 
#undef jniAlglib 
#undef vers
#undef algjavastr
#undef jsonname

#undef targetlow 
#undef targethigh
#define targetlow 3.9
#define targethigh 7.8
#define jsonname(et,end) "_ZN22NativeAlgorithmV1_1_3B23" #et  "JsonAlgorithmContext" #end //Makes one in 5 minutes
#define algLibName "/libnative-algorithm-v1_1_3_B.so";
#define jniAlglib 	"/libnative-algorithm-jni-v113B.so";
#define vers(x) x ##3
#undef algjavastr
#define algjavastr(x) "Java_com_algorithm_v1_11_13_1b_NativeAlgorithmLibraryV1_11_13B_" #x

#include "jnifuncs.hpp"

#endif
#endif

#ifdef NOTCHINESE
#define datahandlestr(x) "Java_com_no_sisense_enanddecryption_CGMDataHandle130_" #x

#define	algDatahandleName "/libdata-handle-lib.so";
algtype(V120SpiltData) V120SpiltData;
algtype(v120RegisterKey) v120RegisterKey;
algtype(V120ApplyAuthentication) V120ApplyAuthentication;
algtype(V120RawData) V120RawData;
algtype(V120Activation) V120Activation;
algtype(V120IsecUpdate) V120IsecUpdate;
static bool getDatahandle() {
	std::string_view alglib=algDatahandleName;
	void *handle=openlib(alglib);
	if(!handle) {
		LOGGER("dlopen %s failed: %s\n",alglib.data(),dlerror());
		return false;
		}
{	constexpr const char str[]=datahandlestr(V120SpiltData);
	V120SpiltData= (algtype(V120SpiltData)) dlsym(handle,str);
	 if(!V120SpiltData) {
	 	LOGGER("dlsym %s failed: %s\n",str,dlerror());
		return false;
	 	}
}

{	constexpr const char str[]=datahandlestr(v120RegisterKey);
	v120RegisterKey= (algtype(v120RegisterKey)) dlsym(handle,str);
	 if(!v120RegisterKey) {
	 	LOGGER("dlsym %s failed: %s\n",str,dlerror());
		return false;
	 	}
}
{	constexpr const char str[]=datahandlestr(V120ApplyAuthentication);
	V120ApplyAuthentication= (algtype(V120ApplyAuthentication)) dlsym(handle,str);
	 if(!V120ApplyAuthentication) {
	 	LOGGER("dlsym %s failed: %s\n",str,dlerror());
		return false;
	 	}
}
{	constexpr const char str[]=datahandlestr(V120RawData);
	V120RawData= (algtype(V120RawData)) dlsym(handle,str);
	 if(!V120RawData) {
	 	LOGGER("dlsym %s failed: %s\n",str,dlerror());
		return false;
	 	}
}
{	constexpr const char str[]=datahandlestr(V120Activation);
	V120Activation= (algtype(V120Activation)) dlsym(handle,str);
	 if(!V120Activation) {
	 	LOGGER("dlsym %s failed: %s\n",str,dlerror());
		return false;
	 	}
}
{	constexpr const char str[]=datahandlestr(V120IsecUpdate);
	V120IsecUpdate= (algtype(V120IsecUpdate)) dlsym(handle,str);
	 if(!V120IsecUpdate) {
	 	LOGGER("dlsym %s failed: %s\n",str,dlerror());
		return false;
	 	}
}

     LOGAR("found datahandle functions");
     return true;
	}
#endif
#ifdef NOTCHINESE
bool siInit2()  {
   static bool init=getNativefunctions2()&&getJNIfunctions2()&&getDatahandle();
   return init;
   }
#endif
bool siInit3()  {
   static bool init=getNativefunctions3()&&getJNIfunctions3() ;
   return init;
   }
 bool siInit(bool notchinese) {
#ifdef NOTCHINESE
   if(notchinese)
      return siInit2();
#endif
   return siInit3();
   };

#include <charconv>
bool loadjson(SensorGlucoseData *sens, const char *statename,const AlgorithmContext *alg, setjson_t setjson) {
   auto *nati=reinterpret_cast<NativeAlgorithm*>(alg ->mNativeContext);
   if(!nati) {
      LOGAR("mNativeContext==null");
      return false;
      }
      sens->mutex.lock();
      Readall json(statename);
      sens->mutex.unlock();
      if(!json.data()) {
         LOGGER("read %s failed\n",statename);
         return false;
         }
#ifndef NOLOG
     int res=
#endif
              setjson(nati,json.data());
     LOGGER("setjson()=%d\n",res);
     return true;
	}

bool savejson(SensorGlucoseData *sens,const string_view name,int index,const AlgorithmContext *alg,getjson_t getjson) {
	if(!getjson) {
		LOGAR("getjson==null");
		return false;
		}
   auto *nati=reinterpret_cast<NativeAlgorithm*>(alg ->mNativeContext);
   if(!nati) {
      LOGAR("mNativeContext==null");
      return false;
      }
	const char *json=getjson(nati);
	LOGGER("getjson()=%p\n",json);
	if(!json) {
		return false;	
		}
	int jsonlen=strlen(json);
	if(!json) {
		LOGAR("jsonlen==0");
		return false;
		}
	const int maxbuf=name.size()+6+2;
	char buf[maxbuf];
   memcpy(buf,name.data(),name.size());
	char *startnum=buf+name.size();
	auto [ptr,ec]  =std::to_chars(startnum,buf+maxbuf,index);
   *ptr='\0';
	bool success=writeall(buf,json,jsonlen);
	if(!success) {
		return false;
		}
	int res;
	{
	std::lock_guard<std::mutex> lock(sens->mutex);
	res=rename(buf,name.data());
	}
	if(res) {
		flerror("rename(%s,%s) failed",buf,name.data());
		return false;
		}
	return true;
	}
#include "sibionics/SiContext.hpp"

void  	SiContext::setNotchinese(SensorGlucoseData *sens) {
   sens->setNotchinese();
    notchinese=true;
#ifdef NOTCHINESE
    this->~SiContext();
   auto res= siInit2();
   algcontext=initAlgorithm2(sens,setjson2);
#endif
   }
SiContext::SiContext(SensorGlucoseData *sens): algcontext(
#ifdef NOTCHINESE
        sens->notchinese()?initAlgorithm2(sens,setjson2):
#endif

        initAlgorithm3(sens,setjson3)),notchinese(sens->notchinese()) {
   	};
SiContext::~SiContext() {
#ifndef NOLOG
	int res=
#endif
	(
#ifdef NOTCHINESE
            notchinese?releaseAlgorithmContext2:
#endif

    releaseAlgorithmContext3)(subenv,nullptr, reinterpret_cast<jobject>(algcontext));
	LOGGER("releaseAlgorithmContext(%p)=%d notchinese=%d\n",algcontext,res,notchinese);
   delete algcontext;
	};
#else
bool siInit() {return false;}
#endif
