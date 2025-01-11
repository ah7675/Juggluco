
#ifdef SIBIONICS
#ifdef NOTCHINESE
#include "config.h"
#include <string_view>
#include <jni.h>
#include <memory>
#include <inttypes.h>
#include "SensorGlucoseData.hpp"
#include "fromjava.h"
#include "logs.hpp"
//#include "sibionics/AlgorithmContext.hpp"
#include "streamdata.hpp"
#include "datbackup.hpp"
#include "inout.hpp"
#include "jnidef.h"
#include "EverSense.hpp"
/*
static auto reverseaddress(const char address[]) {
   std::array<jbyte,6> uitar;
   auto *uit=uitar.data();
   sscanf(address,"%hhX:%hhX:%hhX:%hhX:%hhX:%hhX",uit+5,uit+4,uit+3,uit+2,uit+1,uit);
    return uitar;
   } */

/*
std::array<jbyte,6>  deviceArray(const char address[]) {
   std::array<jbyte,6> uitar;
   auto *uit=uitar.data();
   sscanf(address,"%hhX:%hhX:%hhX:%hhX:%hhX:%hhX",uit+5,uit+4,uit+3,uit+2,uit+1,uit);
   return uitar;
   } */

#ifndef NOLOG
extern void logbytes(std::string_view text,const uint8_t *value,int vallen) ;
#else
#define logbytes(text,value, vallen) 
#endif

extern JNIEnv *subenv;

#include "share/hexstr.hpp"

std::pair<std::unique_ptr<data_t>,int> getActivation(jlong timesec) {
    auto zero=data_t::newex(2);
    zero->clear();
    auto fill=data_t::newex(50);
   int ret=V120Activation(subenv, nullptr,0, true, (jbyteArray) zero, timesec, 1234, (jbyteArray) fill, fill->size());
   hexstr zerohex((uint8_t*)zero->data(),zero->size());
   hexstr fillhex((uint8_t*)fill->data(),ret);
   LOGGER("getActivation(%jd) zero=%s res=%s\n",timesec,zerohex.str(),fillhex.str());
   data_t::deleteex(zero);
   return {std::unique_ptr<data_t>(fill),ret};
   }
extern "C" JNIEXPORT jbyteArray JNICALL   fromjava(getSIActivation)(JNIEnv *env, jclass cl) {
   auto [cmd,len]=getActivation(time(nullptr));
   jbyteArray uit=env->NewByteArray(len);
   env->SetByteArrayRegion(uit, 0, len,cmd.get()->data());
   return uit;
   }



static Data_t getIsecUpdate(jlong timesec) {
    Data_t zero(2);
    zero.clear();
    Data_t fill(50);
   int ret=V120IsecUpdate(subenv, nullptr,0, true,  zero, timesec, fill, fill.data->size());
   hexstr zerohex((uint8_t*)zero.data->data(),zero.data->size());
   hexstr fillhex((uint8_t*)fill.data->data(),ret);
   LOGGER("getIsecUpdate(%jd) zero=%s res=%s\n",timesec,zerohex.str(),fillhex.str());
   fill.used=ret;
   return fill;
   }
extern "C" JNIEXPORT jbyteArray JNICALL   fromjava(getSItimecmd)(JNIEnv *env, jclass cl) {
   auto cmd=getIsecUpdate(time(nullptr));
   int len=cmd.used;
   jbyte *dat=cmd.data->data();
   jbyteArray uit=env->NewByteArray(len);
   env->SetByteArrayRegion(uit, 0, len,dat);
   return uit;
   }

std::array<jbyte,6>  deviceArray(const char address[]);

auto makeauthbytes(const char * address) {
   auto rev=deviceArray(address);
   Data_t jrev(rev);
   Data_t uitar(50); 
   int uitlen = V120ApplyAuthentication(subenv,nullptr,1, true, 0,jrev , uitar, uitar.capacity());
   uitar.used=uitlen;
   return uitar;
   }
extern "C" JNIEXPORT jbyteArray JNICALL   fromjava(siAuthBytes)(JNIEnv *env, jclass cl,jlong dataptr) {
   if(!dataptr)
       return nullptr;
   const SensorGlucoseData *usedhist=reinterpret_cast<streamdata *>(dataptr)->hist ; 
   if(!usedhist)
       return nullptr;
   const auto address=usedhist->deviceaddress();
   const auto data=makeauthbytes(address);
   const auto *dat=data.data->data();
   const int len=data.used;
   logbytes("siAuthBytes",(const uint8_t *)dat,len);
   jbyteArray uit=env->NewByteArray(len);
   env->SetByteArrayRegion(uit, 0, len,dat);
   return uit;
   }

extern Data_t askindexdata(jlong index);
Data_t askindexdata(jlong index) {
   Data_t dat(30);
   Data_t zero(2);
  int res=V120RawData(subenv, nullptr, 0, true, (jbyteArray)zero, index, 0, (jbyteArray)dat, dat.capacity());
   dat.used=res;
  return dat;
   }



#ifdef TEST
#define THREADLOCAL 
#else
#define THREADLOCAL  thread_local
#endif
static THREADLOCAL jlong sprintargs[1024];
static  THREADLOCAL int recordsprint=-1;
#define VISIBLE __attribute__((__visibility__("default")))
extern "C" int VISIBLE  __vSprintf_chk(char * s, int flag, size_t slen, const char * format, va_list args);
extern "C" int VISIBLE  __vsprintf_chk(char * s, int flag, size_t slen, const char * format, va_list args);
extern "C" int VISIBLE  __vSprintf_chk(char * s, int flag, size_t slen, const char * format, va_list args) {
   if(recordsprint>=0) {
       va_list newargs;
       va_copy(newargs, args);
      jlong val=va_arg(newargs, jlong);
      sprintargs[recordsprint++]=val;
      va_end(newargs);
      } 
   int res=__vsprintf_chk( s, flag, slen, format,  args);
   LOGGER(" __vsprintf_chk(%s (%p),%d,%zd,%s,va_list)=%d\n",s,s,flag,slen,format,res); 
   return res;
   }
#include <vector>
extern int sitrend2abbott(int sitrend) ;
extern float sitrend2RateOfChange(int sitrend);

extern uint32_t makestarttime(int index,uint32_t eventTime);

#include "sibionics/json.hpp"
extern bool savejson(SensorGlucoseData *sens,std::string_view, int index,const AlgorithmContext *alg,getjson_t getjson );
extern getjson_t getjson2;
extern jlong glucoseback(uint32_t glval,float drate,SensorGlucoseData *hist) ;

jlong SiContext::processData2(SensorGlucoseData *sens,time_t nowsecs,data_t *data,int sensorindex)  {
   logbytes("processData2 input: ",(const uint8_t *)data->data(),data->size());
   Gegs<jint> jiar(2);
   Data_t jsonuit(7168);
   Data_t bar2(2);

   int i6=data->size();
   memset(jiar.data->data(),'\0',sizeof(jint)*jiar.data->size());
   memset(bar2.data->data(),'\0',bar2.data->size());
   recordsprint=0;
  int res=V120SpiltData(subenv,nullptr,0,(jbyteArray)data,(jintArray)jiar,(jbyteArray)jsonuit,true,(jbyteArray)bar2,i6);
   int recorded=recordsprint;
  recordsprint=-1;
   LOGGER("res=%d\n",res);
   LOGGER("%s\n",jsonuit.data->data());
   logbytes("bar2",(const uint8_t *)bar2.data->data(),bar2.data->size());
   int *idat=jiar.data->data();
   char tmpbuf[80];
   const char str[]="jiar:";
   memcpy(tmpbuf,str,sizeof(str)-1);
   char *ptr=tmpbuf+ sizeof(str)-1;
   for(int i=0;i<jiar.data->size();i++) {
      ptr+=sprintf(ptr," %d",idat[i]);
      }
   *ptr++='\n';
   LOGGERN(tmpbuf,ptr-tmpbuf);
  jlong *basear=sprintargs;
  switch(idat[0]) {
    case 49159: {
      sensor *sensor=sensors->getsensor(sensorindex);
      for(int i=0;i<res;i++) {
        const int maxid=sens->getSiIndex();
        int index=(int) basear[0];
        time_t eventTime=basear[10];
            if(index!=maxid)   {
                if(index<maxid)   {
                   LOGGER("SIprocess index=%d<maxid=%d\n",index,maxid);
                   return 3LL;
                   }
                else {
                   LOGGER("SIprocess index=%d>maxid=%d\n",index,maxid);
                   int maxretry=(index-maxid)<20?2:((index-maxid)<200?5:10);
                   if(sens->retried++<maxretry) {
                      return 3LL;
                      }
                   }
              if(maxid<10) {
                   auto starttime=makestarttime(index,eventTime);
                   sens->getinfo()->starttime=starttime;
                   sensor->starttime=starttime;
                   sensors->setindices();
                   backup->resendResetDevices(&updateone::sendstream);
                   }
           }
            double temp=basear[1]/10.0;
            auto current= basear[2];
            double value=current/10.0;
            LOGGER("current=%" PRId64 " %.1f mmol/L\n",current,value);
            int reindex=(int)basear[4];
            auto trend=(int)basear[6];

    #ifdef USE_PROCESS
            auto newvalue= si.process2(index,value,temp);
            if(newvalue>1.8)
                sens->getinfo()->pollinterval=newvalue-value;
           else {
             if(sens->getinfo()->pollinterval<40) newvalue=value+sens->getinfo()->pollinterval;
             }
    #else
        #define 	newvalue value
    #endif
             const int mgdL=std::round(newvalue*convfactordL);
             const int trend2=algcontext->ig_trend;
            const float change=sitrend2RateOfChange(trend);
            const int abbottrend=sitrend2abbott(trend);
            LOGGER("index=%d temp=%f value=%f newvalue=%f trend=%d (%d) %d %1.f itime=%" PRIu64 " %s" ,index,temp,value,newvalue,trend,trend2,abbottrend,change,eventTime,ctime(&eventTime));

          if(newvalue>1.8&&newvalue<30) {
               sens->savestream(eventTime,index,mgdL,abbottrend,change);
               sens->retried=0;
               if(!reindex)  {
                     sens->sensorerror=false;
                     if(sensor->finished) {
                            sensor->finished=0;
                            LOGGER("SIprocess finished=%d\n", sensor->finished);
                            backup->resensordata(sensorindex);
                            }
                       auto res=glucoseback(mgdL,change,sens);
                       if(!(index%5)) savejson(sens,sens->statefile,index,algcontext,getjson2);
                        backup->wakebackup(Backup::wakestream);
                      extern void wakewithcurrent();
                         wakewithcurrent();

    #ifdef OLDEVERSENSE
                            sendEverSenseold(sens,5);
    #endif
                         return res;
                        }
                     else {
                       if(!(index%500)) {
                            savejson(sens,sens->statefile,index,algcontext,getjson2);
                            backup->wakebackup(Backup::wakestream);
                            }
                          sens->receivehistory=nowsecs;
                         }
                      const int last=sens->pollcount()-1;
                      if(last<sens->getbroadcastfrom()) sens->setbroadcastfrom(last);
                   }
               else {
                if(index==maxid) sens->setSiIndex(maxid+1);
                LOGGER("SIprocess failed: index=%d temp=%f value=%f reindex=%d\n", index, temp, value,reindex);
                if(!reindex&&!(index%5))  {
                      sens->sensorerror=true;
                      return 0LL;
                      }
                    }
                 basear+=11;
                }//for loop
     	    return 1LL;
	    };break;
	case 49165: {
		int type=(int) basear[0];
		switch(type) {
         case 49161: return 9LL;
         case 49156: return 7LL;
		   case 49153: return 4LL;
		   case 49154:  {
		   	if(basear[1]!=1) {
			  int error=(int)basear[2];
			  if(error!=9&&error!=10) return 4LL; 
			  }
			return 6LL;
			};
		    case 49160: return 5LL;
		   };
		
		};break;
//	  case 49161:
	  case 49227: return 6LL;
	   }
      return 8LL; ///?
	}


#else
#include <jni.h>
#include "fromjava.h"
extern "C" JNIEXPORT jbyteArray JNICALL   fromjava(getSIActivation)(JNIEnv *env, jclass cl) {
   return nullptr;
   }
extern "C" JNIEXPORT jbyteArray JNICALL   fromjava(getSItimecmd)(JNIEnv *env, jclass cl) {
   return nullptr;
   }
extern "C" JNIEXPORT jbyteArray JNICALL   fromjava(siAuthBytes)(JNIEnv *env, jclass cl,jlong dataptr) {
   return nullptr;
   }
#endif
#endif
