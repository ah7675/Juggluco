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
/*      Fri Jan 27 15:31:32 CET 2023                                                 */


package tk.glucodata;

import static android.graphics.Color.WHITE;
import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static tk.glucodata.Applic.isWearable;
import static tk.glucodata.Applic.usedlocale;
import static tk.glucodata.Floating.rewritefloating;
import static tk.glucodata.Layout.getMargins;
import static tk.glucodata.settings.Settings.editoptions;
import static tk.glucodata.settings.Settings.removeContentView;
import static tk.glucodata.util.getbutton;
import static tk.glucodata.util.getcheckbox;
import static tk.glucodata.util.getlabel;
import static tk.glucodata.MainActivity.screenheight;

import android.graphics.Color;
import android.os.Build;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import yuku.ambilwarna.AmbilWarnaDialog;

public class FloatingConfig {
private static final String LOG_ID="FloatingConfig";
//   AmbilWarnaDialog(Context context, int color, boolean supportsAlpha, OnAmbilWarnaListener listener)
static private boolean background=true;
static public void	setcolor(int c) {
		Log.i(LOG_ID,"setcolor("+(c&0xFFFFFFFF)+")");
		if(background) { 
			Floating.setbackgroundcolor(c);
			}
		else  {Floating.setforegroundcolor(c);			}
		}
static public int	getcolor() {
		return background?Natives.getfloatingbackground( ):Natives.getfloatingforeground( );
		}



static public void show(MainActivity act,View parent) {
	parent.setVisibility(INVISIBLE);
	int initialColor= getcolor();


	int height=GlucoseCurve.getheight();
	int width=GlucoseCurve.getwidth();
    AmbilWarnaDialog dialog = new AmbilWarnaDialog(act, initialColor,c-> {
	Log.i(LOG_ID,String.format(usedlocale,"col=%x",c));
		setcolor(c);
		//rewritefloating(act);
		Floating.invalidatefloat();
    }, v-> {
    	}
	);
	View view=dialog.getview();
   final String fontstring=Applic.app.getString(R.string.fontsize)+ " ";
	var  sizelabel=getlabel(act,fontstring);


   final int maxfont=height*7/10;

	int currentfont=Natives.getfloatingFontsize();
     if(currentfont<5||currentfont>(int)(screenheight*.8)) {
                currentfont=(int)Notify.glucosesize; 
                }

   SeekBar fontsizeview=new SeekBar(act);
      fontsizeview.setMax((int)((maxfont-5)*100.0));
      fontsizeview.setProgress((int)((currentfont-5)*100.0));
//      var fwidth=(int)(width*0.8f);
      fontsizeview.setMinimumWidth((int)(width*.5));
//      final int minimumvalue=500;
/*    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        fontsizeview.setMin(minimumvalue);
    }*/
    fontsizeview.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
		@Override
		public  void onProgressChanged (SeekBar seekBar, int progress, boolean fromUser) {
//         int newprogress=progress+minimumvalue; 
			var siz=(int)Math.round(progress/100.0)+5;
//         if(doLog) sizelabel.setText(fontstring+siz);
         Natives.setfloatingFontsize(siz);
          rewritefloating(act);
			}
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			Log.i(LOG_ID,"onStartTrackingTouch");
			}
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});



/*
	var  sizeview= new EditText(act);
              sizeview.setImeOptions(editoptions);
                sizeview.setMinEms(4);
                sizeview.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

	int fontsize=Natives.getfloatingFontsize();
	sizeview.setText(fontsize+"");
        TextView.OnEditorActionListener  actlist= new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                                 Log.i(LOG_ID,"onEditorAction");
				 try {
					var siz=Integer.parseInt(String.valueOf(v.getText()));
					int maxfont=height*7/10;
					if(siz>maxfont) {
						Applic.argToaster(act, act.getString(R.string.fonttoolarge)+maxfont, Toast.LENGTH_SHORT);
						return true;
						}
					else  {
						Natives.setfloatingFontsize(siz);
						 rewritefloating(act);
						//Floating.invalidatefloat();
						 }
					}
				catch(Throwable th) {
					Log.stack(LOG_ID,"parseInt",th);
					}
//                                return true;
                           }
                    return false;
                    }};
	sizeview.setOnEditorActionListener(actlist);
   */



	var color=Natives.getfloatingbackground();
	boolean transp= Color.alpha(color)!=0xFF;
	var transparant=getcheckbox(act,R.string.transparent,transp);
	var touch=Natives.getfloatingTouchable();
	var touchable=getcheckbox(act,R.string.touchable,touch);
	if(!background)
			transparant.setVisibility(INVISIBLE);
	else {
		if (transp)
			view.setVisibility(INVISIBLE);
		}
	var close=getbutton(act,R.string.closename);
//	CompoundButton foregroundswitch;

	Layout layout;
	CheckBox floatglucose=new CheckBox(act);
	floatglucose.setText(R.string.active);
	floatglucose.setChecked(Natives.getfloatglucose());
	floatglucose.setOnCheckedChangeListener( (buttonView,  isChecked) -> Floating.setfloatglucose(act,isChecked) ) ;
	var Help=getbutton(act,R.string.helpname);
	Help.setOnClickListener(v-> help.help(R.string.floatingconfig,act));


    var    foregroundbutton = new RadioButton(act);
      var   backgroundbutton = new RadioButton(act);
        foregroundbutton.setText(R.string.foreground);
        backgroundbutton.setText(R.string.background);
	foregroundbutton.setChecked(!background);
	backgroundbutton.setChecked(background);
	backgroundbutton.setTextColor(WHITE);
	foregroundbutton.setTextColor(WHITE);

//    getMargins(backgroundbutton).rightMargin=getMargins(foregroundbutton).leftMargin=(int)(width*0.1);
//	backgroundbutton.setChecked(background);

	var timeshow=getcheckbox(act,R.string.time,Floating.showtime);
	timeshow.setOnCheckedChangeListener( (buttonView,  isChecked) -> {
		Floating.showtime=isChecked;
		Natives.setfloattime(isChecked);
		rewritefloating(act);
		});
	boolean[] hidden={Natives.gethidefloatinJuggluco()};
	var hide=getcheckbox(act,R.string.floatjuggluco, !hidden[0]);
	if(hidden[0]) {
		Floating.makefloat();
		}
	hide.setOnCheckedChangeListener( (buttonView,  isChecked) -> {
		hidden[0]=!isChecked;
		Natives.sethidefloatinJuggluco(!isChecked);
		});



	var leftlayout=new Layout(act,(l, w, h)-> { return new int[] {w,h}; },new View[]{sizelabel},new View[]{fontsizeview},new View[]{foregroundbutton,touchable}, new View[]{backgroundbutton,transparant},new View[]{hide,timeshow,floatglucose},new View[]{Help,close});
	leftlayout.setLayoutParams( new ViewGroup.LayoutParams(WRAP_CONTENT,MATCH_PARENT));
	view.setLayoutParams( new ViewGroup.LayoutParams(MATCH_PARENT,MATCH_PARENT));
   view.setPadding(0,MainActivity.systembarTop,0,0);
   leftlayout.setPadding(0,MainActivity.systembarTop/2,0,0);
	 layout=new Layout(act,(l,w,h)-> { return new int[] {w,h}; }, new View[]{view,leftlayout});
  	layout.setPadding(MainActivity.systembarLeft,0,MainActivity.systembarRight,MainActivity.systembarBottom);
	layout.setBackgroundColor(Applic.backgroundcolor);
	transparant.setOnCheckedChangeListener( (buttonView,  isChecked) -> {
		Floating.setbackgroundalpha(isChecked?0:0xff);
		Floating.invalidatefloat();
		removeContentView(layout);
		act.poponback();
		show(act,parent);
	});
	touchable.setOnCheckedChangeListener( (buttonView,  isChecked) -> {
		Floating.setTouchable(isChecked);
		});
/*
	foregroundswitch.setOnCheckedChangeListener( (buttonView,  isChecked) -> {
		background=isChecked;
		removeContentView(layout);
		act.poponback();
		show(act,parent);

	});*/


        foregroundbutton.setOnCheckedChangeListener( (buttonView,  isChecked) -> {
            Log.i(LOG_ID,"foregroundbutton "+isChecked);
            backgroundbutton.setChecked(!isChecked);
            background=!isChecked;
            removeContentView(layout);
            act.poponback();
            show(act,parent);
            });

        backgroundbutton.setOnCheckedChangeListener( (buttonView,  isChecked) -> {
            Log.i(LOG_ID,"backgroundbutton "+isChecked);
            foregroundbutton.setChecked(!isChecked);
            });





	       act.addContentView(layout, new ViewGroup.LayoutParams(MATCH_PARENT,MATCH_PARENT));
	Button noclose= act.findViewById(R.id.closeambi);
	 if(noclose!=null) {
		noclose.setVisibility(GONE);
		noclose.setText("");
		Button nohelp= act.findViewById(R.id.helpambi);
		nohelp.setText("");
		nohelp.setVisibility(GONE);
	  }
	act.setonback(()-> { 
		parent.setVisibility(VISIBLE);
		removeContentView(layout); 
		if(hidden[0]) {
			Floating.removeFloating();
			}

		});
	close.setOnClickListener(v->{
		act.doonback();
	});
}

}
