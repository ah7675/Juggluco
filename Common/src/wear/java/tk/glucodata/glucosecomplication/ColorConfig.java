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
/*      Fri Oct 11 12:22:15 CEST 2024                                                 */


package tk.glucodata.glucosecomplication;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static tk.glucodata.Applic.usedlocale;
import static tk.glucodata.Natives.getComplicationArrowColor;
import static tk.glucodata.Natives.getComplicationBackgroundColor;
import static tk.glucodata.Natives.getComplicationTextBorderColor;
import static tk.glucodata.Natives.getComplicationTextColor;
import static tk.glucodata.Natives.setComplicationArrowColor;
import static tk.glucodata.Natives.setComplicationBackgroundColor;
import static tk.glucodata.Natives.setComplicationTextBorderColor;
import static tk.glucodata.Natives.setComplicationTextColor;
import static tk.glucodata.Specific.useclose;
import static tk.glucodata.settings.Settings.removeContentView;
import static tk.glucodata.util.getbutton;
import static tk.glucodata.util.getcheckbox;
import static tk.glucodata.util.getlabel;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Space;

import tk.glucodata.BackGesture;
import tk.glucodata.Log;
import tk.glucodata.Applic;
import tk.glucodata.GlucoseCurve;
import tk.glucodata.Layout;
import tk.glucodata.MainActivity;
import tk.glucodata.R;
import yuku.ambilwarna.AmbilWarnaDialog;

public class ColorConfig {
//   static private final int[] names=new int[]{R.string.arrow,R.string.text,R.string.textborder, R.string.background};
   static private final int[] names=new int[]{R.string.arrow,R.string.text, R.string.background};
   private static final String LOG_ID="ColorConfig";
   public static  RadioButton getradiobutton(Context context, int res) {
         var radio=new RadioButton(context);
         radio.setText(res);
   //      radio.setChecked(value);
         return radio;
         }

   static boolean hasdefault(int val) {
      return val==0;
      }
   static private int getcolor(int type) {
       return switch (type) {
           case 0 -> getComplicationArrowColor();
           case 1 -> getComplicationTextColor();
           case 2 -> getComplicationBackgroundColor();
           default -> {
               Log.e(LOG_ID, "getcolor unknown type " + type);
               yield 0;
           }
       };
      }
//         0xff00ffff,
//0xff305cde //Royal Blue
//0xff6395ee //Corn
//0xff82c8e5 //Sky
//         0xff90d5ff,
    final  static int[] defcol={
         0xff82c8e5,
         0xffffffff,
         0xff000000};
   static private int getcolordef(int type) {
      int col= getcolor(type) ;
      return col==0?defcol[type]:col;
      }


   static private void setcolor(int type,int color) {
      switch(type) {
         case 0: setComplicationArrowColor(color);return;
         case 1: setComplicationTextColor(color);return;
         case 2: setComplicationBackgroundColor(color);return;
         default:
               Log.e(LOG_ID, "setcolor unknown type " + type);
         }
      }

   private static void setradio(RadioButton[] radios,  CheckBox defbox) {
         for(RadioButton but:radios) {
            but.setOnCheckedChangeListener( (buttonView,  isChecked) -> {
               if(isChecked) {
                  int i=0;
                  for(RadioButton b:radios)  {
                      if(b==buttonView) {
                           radiosel=i;
                           int col=getcolor(i);
                           defbox.setChecked(hasdefault(col));
                           }
                       else {
                           b.setChecked(false);
                           }
                     ++i;
                     }
                     }
               });
         }
        }
static private int radiosel=0;
static public   void show(MainActivity context, View view) {
	   view.setVisibility(INVISIBLE);
      var allradio=new RadioButton[names.length];
        for(int i=0;i<names.length;++i) 
            allradio[i]=getradiobutton(context,names[i]);

      allradio[radiosel].setChecked(true);
    int selcolor=getcolor( radiosel);
      var defaultbox=getcheckbox(context,R.string.defaultname,hasdefault(selcolor));


	defaultbox.setOnCheckedChangeListener( (buttonView,  isChecked) -> {
		if(isChecked) {
         final int curcol=radiosel;
			setcolor(curcol,0);
          if(curcol==2) {
             GlucoseValue.newbackground=0;
             }
			}
		});
      setradio(allradio,defaultbox);
      var select=getbutton(context,R.string.modify);
      select.setOnClickListener(v-> {
            showcolors(context,defaultbox);
            });

      var close=getbutton(context,R.string.closename);
//   var density=tk.glucodata.GlucoseCurve.metrics.density;
/*	allradio[1].setPadding(0,0,0,0);
	allradio[2].setPadding(0,0,(int)(density*22.0),0);
	allradio[3].setPadding(0,0,(int)(density*5.0),0);; */
//	allradio[0].setPadding((int)(density*10.0),0,0,0); 
//	allradio[1].setPadding(0,0,(int)(density*20.0f),0);
   var space1=new Space(context);
   var space2=new Space(context);
//      Layout layout=new Layout(context,(l, w, h)-> { return new int[] {w,h}; },new View[]{head},new View[]{new Space(context),allradio[1],allradio[2]},new View[]{allradio[0],allradio[3]},new View[]{defaultbox,select},  new View[]{close});

	if(!useclose) close.setVisibility(INVISIBLE);
      Layout layout=new Layout(context,(l, w, h)-> { return new int[] {w,h}; },new View[]{allradio[2]},new View[]{space1,allradio[0],allradio[1],space2},new View[]{defaultbox,select},  new View[]{close});
      layout.setBackgroundColor(Applic.backgroundcolor);
   var density=tk.glucodata.GlucoseCurve.metrics.density;
	layout.setPadding(0,(int)(density*7.0),0,0);
      close.setOnClickListener(v-> {});
      MainActivity.setonback(()-> {
         removeContentView(layout); 
	      view.setVisibility(VISIBLE);
			 tk.glucodata.glucosecomplication.GlucoseValue.updateall();
         });
      close.setOnClickListener(v->{
         MainActivity.doonback();
      });
       context.addContentView(layout, new ViewGroup.LayoutParams(MATCH_PARENT,MATCH_PARENT));
      };

   static public void showcolors(MainActivity act,CheckBox def) {
      var glview=new GlucoseValue(150,150);
      int coltype=radiosel;
      int initialColor=getcolordef(coltype);
      int height= GlucoseCurve.getheight();
      var width=GlucoseCurve.getwidth();
      final var preview=new ImageView(act);
      AmbilWarnaDialog dialog = new AmbilWarnaDialog(act, initialColor, c-> {
         Log.i(LOG_ID,String.format(usedlocale,"col=%x",c));
         setcolor(coltype,c);
         def.setChecked(false);
	  if(coltype==2) {
	     GlucoseValue.newbackground=c;
	     }
	 preview.setImageBitmap(glview.previewbitmap());
       }, v-> {
            int h=v.getMeasuredHeight();
                int w=v.getMeasuredWidth();
                v.setY((int)((height-h)*.5));
                v.setX((int)((width-w)*.57));
         });
   final var head=getlabel(act,names[radiosel]);
   var close=getbutton(act,R.string.closename);
   View view=dialog.getview();
   preview.setImageBitmap(glview.previewbitmap());
   view.setLayoutParams( new ViewGroup.LayoutParams((int)(width*0.72), (int)(height*0.72)));
   var density=tk.glucodata.GlucoseCurve.metrics.density;
   preview.setPadding(0,(int)(density*18.0),0,0);
   SeekBar fontsizeview;
   if(radiosel==1) {
      fontsizeview=new SeekBar(act);
      float maxfont=glview.fontsize;
      float currentfont= Math.min(maxfont, GlucoseValue.upperboundfontsize);
      fontsizeview.setMax((int)(maxfont*100.0));
      fontsizeview.setProgress((int)(currentfont*100.0));
      final int fwidth=(int)(0.7f*height);
      fontsizeview.setMinimumWidth(fwidth);
      fontsizeview.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
		@Override
		public  void onProgressChanged (SeekBar seekBar, int progress, boolean fromUser) {
			var newup=(float)(progress/100.0);
			GlucoseValue.upperboundfontsize =newup>0.99f*glview.fontsize?1000.0f:newup;
			Log.i(LOG_ID,"onProgressChanged "+progress+" "+glview.upperboundfontsize);
			}
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			Log.i(LOG_ID,"onStartTrackingTouch");
			}
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			Log.i(LOG_ID,"onStopTrackingTouch");
			glview.clear();
			preview.setImageBitmap(glview.previewbitmap());
			}
		});

	}
else {
   fontsizeview=null;
	}

	if(!useclose) close.setVisibility(GONE);

      Layout layout=new Layout(act,(l, w, h)-> { return new int[] {w,h}; },new View[]{head},new View[]{view},new View[]{preview},fontsizeview==null?null:new View[]{fontsizeview},new View[]{close});

	var scroll=new ScrollView(act);
	scroll.addView(layout);
	scroll.setFillViewport(true);
	scroll.setSmoothScrollingEnabled(false);
   scroll.setScrollbarFadingEnabled(false);
   scroll.setVerticalScrollBarEnabled(true);
      act.addContentView(scroll,  new ViewGroup.LayoutParams(MATCH_PARENT,MATCH_PARENT));
      scroll.setBackgroundColor(Applic.backgroundcolor);
//        scroll.setOnTouchListener(new BackGesture(act));
      MainActivity.setonback(()-> { 
         removeContentView(scroll); 

         });

      close.setOnClickListener(v->{
         MainActivity.doonback();
      });
      Button noclose= act.findViewById(R.id.closeambi);
       if(noclose!=null) {
         noclose.setVisibility(GONE);
        noclose.setText("");
         Button nohelp= act.findViewById(R.id.helpambi);
         nohelp.setText("");
         nohelp.setVisibility(GONE);
        }
      };
}
