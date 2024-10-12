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
/*      Fri Jan 27 15:26:08 CET 2023                                                 */


package tk.glucodata;


import static tk.glucodata.Applic.isRelease;
import static tk.glucodata.Natives.getlibreAccountIDnumber;
import static tk.glucodata.Natives.processbar;
import static tk.glucodata.Natives.processint;

import java.security.SecureRandom;

class libre3init {
private static		final  String LOG_ID="libre3init";
static void init() {
if(true) {
	if(BuildConfig.doLog==1) 
	{
	new Thread (() -> {
		int resp1=Natives.processint(1,null,null);
		final ECDHCrypto cryptolib=new ECDHCrypto();
		cryptolib.initECDH(null ,1);


		byte[] rdtData= {(byte)0x01,(byte)0x51,(byte)0x41,(byte)0x08,(byte)0x93,(byte)0x4C,(byte)0x00,(byte)0x7A,(byte)0xE0,(byte)0xD1,(byte)0x4A,(byte)0x04,(byte)0x88,(byte)0x1A,(byte)0xCC,(byte)0x74,(byte)0xEE,(byte)0xC1,(byte)0xD7,(byte)0x79,(byte)0x1F,(byte)0xB8,(byte)0x88,(byte)0x05,(byte)0x13,(byte)0x74,(byte)0x10,(byte)0xD1,(byte)0x05,(byte)0x75,(byte)0x25,(byte)0xAF,(byte)0x22,(byte)0x93,(byte)0x24,(byte)0xFC,(byte)0x0B,(byte)0x8C,(byte)0x63,(byte)0x47,(byte)0x31,(byte)0x7B,(byte)0x4A,(byte)0x03,(byte)0x2E,(byte)0x0F,(byte)0xEA,(byte)0xBA,(byte)0x87,(byte)0xDE,(byte)0xA3,(byte)0x04,(byte)0x52,(byte)0x71,(byte)0x8E,(byte)0x47,(byte)0x5E,(byte)0x71,(byte)0x20,(byte)0x8B,(byte)0x84,(byte)0x6B,(byte)0xEA,(byte)0xAC,(byte)0x15,(byte)0xE0,(byte)0xB2,(byte)0x4C,(byte)0x79,(byte)0x43,(byte)0xB7,(byte)0xAA,(byte)0xDE,(byte)0xB4,(byte)0x5C,(byte)0x3C,(byte)0xA7,(byte)0x6D,(byte)0xBC,(byte)0x26,(byte)0xAE,(byte)0xC2,(byte)0x76,(byte)0x43,(byte)0xE1,(byte)0xF0,(byte)0xF9,(byte)0xE5,(byte)0x1A,(byte)0xC3,(byte)0x39,(byte)0xF0,(byte)0x71,(byte)0x2D,(byte)0x3D,(byte)0x11,(byte)0x7B,(byte)0xB4,(byte)0xF2,(byte)0x71,(byte)0x91,(byte)0xFB,(byte)0xD7,(byte)0x70,(byte)0x2F,(byte)0x4C,(byte)0xD6,(byte)0x81,(byte)0xB7,(byte)0x03,(byte)0x58,(byte)0x21,(byte)0x87,(byte)0xFB,(byte)0x81,(byte)0xA2,(byte)0x85,(byte)0x36,(byte)0x5B,(byte)0xE2,(byte)0xEC,(byte)0x18,(byte)0xFD,(byte)0x4C,(byte)0x2E,(byte)0xB5,(byte)0x46,(byte)0xE6,(byte)0x5F,(byte)0xEB,(byte)0x08,(byte)0xB9,(byte)0x1A,(byte)0xAE,(byte)0xFB,(byte)0x08,(byte)0x06,(byte)0x98,(byte)0x9B,(byte)0xFF};
		cryptolib.setPatchCertificate(rdtData);
		 var evikeys=Natives.processbar(5,null,null);

		byte[] data6={(byte)0x04,(byte)0xF3,(byte)0x9D,(byte)0x2D,(byte)0xF9,(byte)0xDA,(byte)0xB5,(byte)0x78,(byte)0xCA,(byte)0xC7,(byte)0x2B,(byte)0xAA,(byte)0xE2,(byte)0x7F,(byte)0xF1,(byte)0xEC,(byte)0x27,(byte)0x18,(byte)0x34,(byte)0x35,(byte)0x91,(byte)0x19,(byte)0x8B,(byte)0x52,(byte)0x10,(byte)0x70,(byte)0x25,(byte)0x98,(byte)0xA5,(byte)0x28,(byte)0x65,(byte)0xC3,(byte)0x1E,(byte)0xF4,(byte)0x65,(byte)0x9B,(byte)0x57,(byte)0x6C,(byte)0xC2,(byte)0xFB,(byte)0xB5,(byte)0x41,(byte)0x6D,(byte)0x06,(byte)0x77,(byte)0x5E,(byte)0xD6,(byte)0x5E,(byte)0x04,(byte)0xA9,(byte)0xBF,(byte)0x0F,(byte)0x3E,(byte)0x9D,(byte)0x45,(byte)0x73,(byte)0x8A,(byte)0xE4,(byte)0x85,(byte)0x60,(byte)0x49,(byte)0x81,(byte)0x2A,(byte)0x81,(byte)0x1B};

	  	Natives.processint(6,data6,null);
	  final byte[] nonce1={(byte)0xA5,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x92,(byte)0xD0,(byte)0x44};
	  byte[] uit= {(byte)0xC6,(byte)0x57,(byte)0xA6,(byte)0x92,(byte)0xE5,(byte)0x7C,(byte)0x63,(byte)0xF2,(byte)0xC9,(byte)0xA1,(byte)0x99,(byte)0x22,(byte)0xBE,(byte)0xDD,(byte)0x9E,(byte)0xF4,(byte)0x21,(byte)0x24,(byte)0x27,(byte)0xC0,(byte)0xC9,(byte)0x74,(byte)0xEC,(byte)0xA3,(byte)0x70,(byte)0x91,(byte)0x77,(byte)0x59,(byte)0x14,(byte)0xF9,(byte)0xBC,(byte)0x3D,(byte)0xF4,(byte)0x89,(byte)0x54,(byte)0x99};
		var encrypted= Natives.processbar(7,nonce1,uit);
     byte[] nonce={(byte)0xA6,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x12,(byte)0xC2,(byte)0xC3};
byte[] bytes60={(byte)0x4A,(byte)0xF5,(byte)0x6F,(byte)0xA6,(byte)0x46,(byte)0x70,(byte)0xF9,(byte)0x0D,(byte)0x7F,(byte)0x37,(byte)0x54,(byte)0x18,(byte)0x4B,(byte)0x45,(byte)0x7B,(byte)0xF4,(byte)0x43,(byte)0x8C,(byte)0x2F,(byte)0xEF,(byte)0xCA,(byte)0xED,(byte)0x32,(byte)0xCE,(byte)0xE3,(byte)0x1F,(byte)0x95,(byte)0x49,(byte)0xFD,(byte)0x42,(byte)0x34,(byte)0xB0,(byte)0x3F,(byte)0x65,(byte)0x9C,(byte)0x8C,(byte)0xCE,(byte)0x49,(byte)0x5B,(byte)0xA5,(byte)0xE7,(byte)0xA7,(byte)0x23,(byte)0xCC,(byte)0xD3,(byte)0xB3,(byte)0x89,(byte)0x4F,(byte)0xC7,(byte)0x6E,(byte)0xB1,(byte)0x0E,(byte)0x87,(byte)0x1A,(byte)0xF4,(byte)0x86,(byte)0xE4,(byte)0x63,(byte)0x15,(byte)0x3C};



	byte[] decr=Natives.processbar(8,nonce,bytes60);
	byte[] AuthKey=Natives.processbar(9,null,null);


		Log.i(LOG_ID, "end test");
		}).start();
		}; 
		}
	};

/*
Log.i(LOG_ID,"init");
if(BuildConfig.doLog==1) {
Log.i(LOG_ID,"testproc");
new Thread (() -> {
for(int i=0;i<5000;i++) {
	var res2=processint(1,null,null);
//	Log.i(LOG_ID, "Natives.processint(1, 0, null)="+res2);
	byte[] input={(byte)0x01,(byte)0x7A,(byte)0x4E,(byte)0xD5,(byte)0x57,(byte)0x4B,(byte)0x00,(byte)0x7A,(byte)0xE0,(byte)0xB5,(byte)0x4A,(byte)0x04,(byte)0x50,(byte)0x1F,(byte)0x0D,(byte)0x09,(byte)0xE2,(byte)0xA1,(byte)0xF2,(byte)0xF5,(byte)0x3B,(byte)0xF0,(byte)0xF9,(byte)0x47,(byte)0x3D,(byte)0xF1,(byte)0xB7,(byte)0x8D,(byte)0xAA,(byte)0xE9,(byte)0xF7,(byte)0x0E,(byte)0x03,(byte)0x37,(byte)0xE9,(byte)0x20,(byte)0xDC,(byte)0xF9,(byte)0xCE,(byte)0x55,(byte)0xC7,(byte)0xA8,(byte)0x04,(byte)0xDA,(byte)0x33,(byte)0xDC,(byte)0x3C,(byte)0x3D,(byte)0xDC,(byte)0x38,(byte)0xE6,(byte)0x03,(byte)0xD1,(byte)0x61,(byte)0x1C,(byte)0x1B,(byte)0x65,(byte)0xFC,(byte)0x8A,(byte)0xF5,(byte)0xAB,(byte)0x24,(byte)0x7B,(byte)0xE4,(byte)0x42,(byte)0x20,(byte)0x95,(byte)0x1B,(byte)0xCA,(byte)0x6C,(byte)0x84,(byte)0x3A,(byte)0x69,(byte)0x4B,(byte)0xEA,(byte)0x71,(byte)0x4E,(byte)0xB7,(byte)0xE4,(byte)0x31,(byte)0x0F,(byte)0x91,(byte)0x25,(byte)0x8F,(byte)0x58,(byte)0x4C,(byte)0x1A,(byte)0x4A,(byte)0x31,(byte)0xF6,(byte)0xE2,(byte)0x90,(byte)0x39,(byte)0xE2,(byte)0xA6,(byte)0x26,(byte)0x4F,(byte)0x60,(byte)0x84,(byte)0x70,(byte)0x02,(byte)0xFA,(byte)0x06,(byte)0x20,(byte)0x2E,(byte)0x60,(byte)0x7A,(byte)0xE9,(byte)0x7E,(byte)0x6D,(byte)0xA6,(byte)0x1C,(byte)0x26,(byte)0x95,(byte)0x52,(byte)0x3B,(byte)0x30,(byte)0xEE,(byte)0xD5,(byte)0x61,(byte)0xEA,(byte)0x4B,(byte)0xF1,(byte)0x3D,(byte)0x47,(byte)0xA3,(byte)0x47,(byte)0x0A,(byte)0xA0,(byte)0x6D,(byte)0xCC,(byte)0xEB,(byte)0x10,(byte)0x7D,(byte)0xE5,(byte)0x18,(byte)0x78,(byte)0x10,(byte)0x94,(byte)0x4F};
		int res= Natives.processint(4, input, null);
//		Log.i(LOG_ID,"Natives.processint(4, input, null)="+res);
	 }
	}).start();
	}
	*/
}
