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


package tk.glucodata.settings;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
import static android.content.pm.PackageManager.DONT_KILL_APP;
import static android.graphics.Color.BLACK;
import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.widget.Spinner.MODE_DIALOG;
import static android.widget.Spinner.MODE_DROPDOWN;
import static androidx.core.os.LocaleListCompat.getEmptyLocaleList;
import static tk.glucodata.Applic.isWearable;
import static tk.glucodata.Backup.getnumedit;
import static tk.glucodata.Natives.getInvertColors;
import static tk.glucodata.Natives.getRTL;
import static tk.glucodata.Natives.getshowhistories;
import static tk.glucodata.Natives.getshownumbers;
import static tk.glucodata.Natives.getshowscans;
import static tk.glucodata.Natives.getshowstream;
import static tk.glucodata.Natives.setthreshold;
import static tk.glucodata.NumberView.avoidSpinnerDropdownFocus;
import static tk.glucodata.RingTones.EnableControls;
import static tk.glucodata.Specific.useclose;
import static tk.glucodata.help.help;
import static tk.glucodata.util.getbutton;
import static tk.glucodata.util.getcheckbox;
import static tk.glucodata.util.getlabel;
import static tk.glucodata.util.getlocale;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.text.InputType;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.core.widget.NestedScrollView;

//import com.google.android.material.slider.LabelFormatter;
//import com.google.android.material.slider.RangeSlider;

import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.List;

import tk.glucodata.Applic;
import tk.glucodata.Backup;
import tk.glucodata.BuildConfig;
import tk.glucodata.Floating;
import tk.glucodata.GlucoseCurve;
import tk.glucodata.HealthConnection;
import tk.glucodata.LabelAdapter;
import tk.glucodata.Layout;
import tk.glucodata.Libreview;
import tk.glucodata.Log;
import tk.glucodata.MainActivity;
import tk.glucodata.Menus;
import tk.glucodata.Natives;
import tk.glucodata.Notify;
import tk.glucodata.R;
import tk.glucodata.Specific;

import java.text.DecimalFormat;
import java.util.Locale;

public class Settings  {
private final static boolean RU=true,SPANISH=false;
private final static String LOG_ID="Settings";
MainActivity activity;

/*
public static String    oldfloat2string(Float get) {
    return get.toString();
} */
private static final DecimalFormat df1 = new DecimalFormat("#.#",new DecimalFormatSymbols(Locale.US));
public static String    float2string(Float get) {
    return df1.format(get);
} 
/*
public static String    float2string(Float get) {
      return String.format("%.1f",get);
	}  */
boolean IntentscanEnabled() {
	try{
	Application app= activity.getApplication();
	  PackageManager manage = app.getPackageManager();
	ComponentName  scan= new ComponentName(app, "tk.glucodata.glucodata");
	return manage.getComponentEnabledSetting(scan)!=COMPONENT_ENABLED_STATE_DISABLED;
	}
	catch (Throwable e) {

		Log.stack(LOG_ID,e);
	}
	return false;
	}
void EnableIntentScanning(boolean val) {
	try{
	Application app= activity.getApplication();
	  PackageManager manage = app.getPackageManager();
	ComponentName  scan= new ComponentName(app, "tk.glucodata.glucodata");
	int com=val?COMPONENT_ENABLED_STATE_ENABLED:COMPONENT_ENABLED_STATE_DISABLED;
   	manage.setComponentEnabledSetting(scan,com , DONT_KILL_APP);
	}
	catch (Throwable e) {

		Log.stack(LOG_ID,e);
	}
	}
static private Settings thisone=null;
public static void set(MainActivity act) {
	act.lightBars(false);
	thisone=new Settings();

	if(!isWearable&&!Natives.getsystemUI()) {
		act.showui=true;
		act.showSystemUI();
		thisone.makesettings(act);
		}
	else
		thisone.makesettingsin(act);

	}
private void makesettingsin(MainActivity act) {
    	activity=act;

        colorwindowbackground=Applic.backgroundcolor;
	boolean[] issaved={false};
   	mksettings(activity,issaved);

	activity.setonback(() -> {

		hidekeyboard();
		finish();
   		act.lightBars(!getInvertColors( ));
		if(tk.glucodata.Menus.on)
			tk.glucodata.Menus.show(activity);

		});
}
private void makesettings(MainActivity act) {
	Applic.app.getHandler().postDelayed( ()->{ makesettingsin(act);},1);
		
}


void recreate(boolean[] issaved) {
  issaved[0]=true;
// removeContentView(settinglayout);
    layoutweg();
   mksettings(activity,issaved);

   }
void layoutweg() {
/* 	removeContentView(settinglayout);
	settinglayout=null;*/
	}
public static void closeview() {
	if(thisone!=null)
		thisone.finish();
	}
static void hideSystemUI() {
	}
void finish() {
	layoutweg();
	settinglayout.setVisibility(GONE);
	
	try {
		activity.setRequestedOrientation(Natives.getScreenOrientation());
		}
        catch(       Throwable  error) {
		String mess=error!=null?error.getMessage():null;
		if(mess==null) {
			mess="error";
			}
	       Log.e(LOG_ID ,mess);
	   }
//	if(editlabel!=null) removeContentView(editlabel) ;
	removeContentView(settinglayout);
	thisone=null;


	if(!isWearable) {
	activity.showui=false;
//   activity.hideSystemUI();

	if(!Natives.getsystemUI()) {
		Applic.app.getHandler().postDelayed( ()->{
					activity.hideSystemUI();
					},1);
		}
		}
	activity.requestRender();
	}

//    Button deletelabel;
public static	int editoptions=(isWearable?0:(EditorInfo.IME_FLAG_NO_EXTRACT_UI| EditorInfo.IME_FLAG_NO_FULLSCREEN))| EditorInfo.IME_ACTION_DONE;

 int colorwindowbackground;

static int getbackgroundcolor(Context context) {
    TypedValue typedValue = new TypedValue();
    if (context.getTheme().resolveAttribute(android.R.attr.windowBackground, typedValue, true) && typedValue.type >= TypedValue.TYPE_FIRST_COLOR_INT && typedValue.type <= TypedValue.TYPE_LAST_COLOR_INT) {
        return typedValue.data;
    } else
        return Color.RED;
}

//HorizontalScrollView settinglayout=null;
FrameLayout settinglayout=null;
    RadioButton mmolL;
    RadioButton mgdl;

static View[] mkalarm(MainActivity context,String label1,boolean show,Float value,int kind) {

       
    CheckBox yeslow = new CheckBox(context);
    yeslow.setText(label1);
    EditText alow = new EditText(context);

    final int minheight= GlucoseCurve.dpToPx(48);
    alow.setMinimumHeight(minheight);
	alow.setImeOptions(editoptions);
    alow.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
//    alow.setImeOptions(editoptions);
    alow.setMinEms(2);
    Button ring=getbutton(context,R.string.ringtonename);


    if(show)  {
         yeslow.setChecked(true);
	    }
   else {
        ring.setVisibility(INVISIBLE);
	    alow.setVisibility(INVISIBLE);
	    }

    alow.setText( float2string(value));
yeslow.setOnCheckedChangeListener(
                 (buttonView,  isChecked) -> {
		 if(isChecked) {
            		alow.setVisibility(VISIBLE);
            		ring.setVisibility(VISIBLE);
			}
		else {
            ring.setVisibility(INVISIBLE);
		    alow.setVisibility(INVISIBLE);
		    }
    });

    return new View[]{yeslow, alow,ring};
    }
public static float str2float(String str) {
	if(str!=null) {
	     try {
		return Float.parseFloat(str);
                } catch(Exception e) {};
		}
	return 0.0f;
  }
public static float edit2float(EditText ed) {
	return str2float(ed.getText().toString());
	}

void hidekeyboard() {
 tk.glucodata.help.hidekeyboard(activity) ;
 }
boolean scanenabled=true;
//int addindex=-1;

//EditText glow, ghigh ,tlow,thigh;
EditText tlow,thigh;
static float round(float value,float size)  {
   return Math.round(value*size)/size;
   }
void setvalues() {
      final var unit= Natives.getunit();
        switch(unit) {
            case 1: mmolL.setChecked(true);break;
            case 2: mgdl.setChecked(true);break;
        }

//        alow.setText( float2string(value));
	}


static public void alarmsettings(MainActivity context,View parview,boolean[] issaved) {
	parview.setVisibility(GONE);
	TextView alarmlow,alarmhigh;
        View[] lowalarm= mkalarm(context,context.getString(R.string.lowglucosealarm),Natives.hasalarmlow(),Natives.alarmlow(),0);
        View[] highalarm=mkalarm(context,context.getString(R.string.highglucosealarm),Natives.hasalarmhigh(),Natives.alarmhigh(),1);
	alarmlow=(TextView)lowalarm[1];
	alarmhigh=(TextView)highalarm[1];
	alarmlow.setText( float2string(Natives.alarmlow()));
	alarmhigh.setText( float2string(Natives.alarmhigh()));
        CheckBox isvalue = new CheckBox(context);
	final boolean hasvalue=Natives.hasvaluealarm();
       isvalue.setChecked(hasvalue); //Value
        isvalue.setText(R.string.valueavailablenotification);
	Button ringisvalue=getbutton(context,R.string.ringtonename);
	Button help=getbutton(context,R.string.helpname);
        help.setOnClickListener(v->{help(R.string.alarmhelp,(MainActivity)(v.getContext())); });
       if(!hasvalue) ringisvalue.setVisibility(INVISIBLE);
	isvalue.setOnCheckedChangeListener(
			 (buttonView,  isChecked) -> {
			 if(isChecked) {
				ringisvalue.setVisibility(VISIBLE);
				}
			else {
				ringisvalue.setVisibility(INVISIBLE);
			    }
			});


	var usealarm=getcheckbox(context, R.string.USE_ALARM, Natives.getUSEALARM());
	/*
	usealarm.setOnCheckedChangeListener(
			 (buttonView,  isChecked) -> {
				Natives.setUSEALARM(isChecked);
			    }
			); */
	final boolean alarmloss= Natives.hasalarmloss();
		CheckBox lossalarm = new CheckBox(context);
		lossalarm.setChecked(alarmloss); //Value
        lossalarm.setText(R.string.lossofsignalalarm);
	Button ringlossalarm=getbutton(context,R.string.ringtonename);
		EditText losswait = new EditText(context);
		losswait.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
//    losswait.setImeOptions(editoptions);
		losswait.setImeOptions(editoptions);
		losswait.setMinEms(2);
		int waitloss=Natives.readalarmsuspension(4);
		losswait.setText(waitloss+"");

		var min=getlabel(context,R.string.minutes);
		lossalarm.setOnCheckedChangeListener(
				(buttonView,  isChecked) -> {
					if(isChecked) {
						ringlossalarm.setVisibility(VISIBLE);
						losswait.setVisibility(VISIBLE);
						min.setVisibility(VISIBLE);

					}
					else {
						ringlossalarm.setVisibility(INVISIBLE);
						losswait.setVisibility(INVISIBLE);
						min.setVisibility(INVISIBLE);
					}
				});
	if(!alarmloss) {
		ringlossalarm.setVisibility(INVISIBLE);
		losswait.setVisibility(INVISIBLE);
		min.setVisibility(INVISIBLE);
		}


	var Save=getbutton(context,R.string.save);
	var Cancel=getbutton(context,R.string.cancel);
	View[][] views;
   /*
        var graphrange=new RangeSlider(context);
        graphrange.setValueFrom(0);
         graphrange.setValueTo(500f);
         graphrange.setValues(round(Natives.graphlow(),10.0f),round(Natives.graphhigh(),10.0f)); */
	if(isWearable) {
		var ala=getlabel(context,R.string.alarms);
		final   int pad=(int)(tk.glucodata.GlucoseCurve.metrics.density*9.0);
      var lowspace=new Space(context);
      var highspace=new Space(context);
   		ala.setPadding(pad,pad,pad,pad);
		views=new View[][]{new View[]{ala},new View[]{lowalarm[0]},new View[]{lowspace,lowalarm[1],lowalarm[2]}, new View[]{highalarm[0]},new View[]{highspace,highalarm[1],highalarm[2]},
new View[]{lossalarm},new View[]{losswait,min,ringlossalarm},
new View[]{isvalue},new View[]{ringisvalue,Cancel},new View[]{usealarm},new View[]{Save}};
//new View[]{isvalue},new View[]{ringisvalue},new View[]{Cancel,Save}, new View[] {toucheverywhere}};
		}
	else {
      View[] lostrow={lossalarm,losswait,min,ringlossalarm};
		View[] row6={usealarm,isvalue, ringisvalue};
		View[] rowshow={help,Cancel,Save};
      /*
      var seekbar=new SeekBar(context);
      seekbar.setMax(100);
      seekbar.setProgress(4);
      seekbar.setMinimumWidth((int) (context.curve.getWidth()*0.8f)); */

		views=new View[][]{lowalarm,highalarm,lostrow,row6,rowshow};
		}	
	View lay;
        Layout layout = new Layout(context, (l, w, h) -> {
    		hideSystemUI();
		int[] ret={w,h};
		return ret;
		},views);
   if(isWearable) {
//       layout.setPadding(0, (int) (GlucoseCurve.metrics.density*10),0,0);
      final int sidepad=(int)(GlucoseCurve.metrics.density*5);
       layout.setPadding((int)(GlucoseCurve.metrics.density*2), sidepad,sidepad,sidepad);
       }
     else
        layout.setPadding(MainActivity.systembarLeft,MainActivity.systembarTop,MainActivity.systembarRight,MainActivity.systembarBottom);
	var scroll=new ScrollView(context);	
	scroll.addView(layout);
	scroll.setFillViewport(true);
	scroll.setSmoothScrollingEnabled(false);
   scroll.setScrollbarFadingEnabled(true);
   scroll.setVerticalScrollBarEnabled(Applic.scrollbar);
	lay=scroll;
	/*
	if(isWearable) {
		}
	else
		lay=layout; */
        lay.setBackgroundColor(Applic.backgroundcolor);
	context.addContentView(lay, new ViewGroup.LayoutParams( MATCH_PARENT ,MATCH_PARENT));


	lowalarm[2].setOnClickListener(v->{
		new tk.glucodata.RingTones(0).mkviews(context,context.getString(R.string.lowglucosealarm),lay);
		});
	highalarm[2].setOnClickListener(v->{
		new tk.glucodata.RingTones(1).mkviews(context,context.getString(R.string.highglucosealarm),lay);
		});

	context.setonback(() -> {
		parview.setVisibility(VISIBLE);
		tk.glucodata.help.hidekeyboard(context);
		removeContentView(lay) ;
		});
	Cancel.setOnClickListener(
		v->{
		parview.setVisibility(VISIBLE);
	   	context.poponback();
			tk.glucodata.help.hidekeyboard(context);
		removeContentView(lay) ;
		});

    Save.setOnClickListener(v->{
	  final boolean hasloss= lossalarm.isChecked();
	if(hasloss) {
		String str = losswait.getText().toString();
	     try  {
		if(str != null) {
		     short wa = Short.parseShort(str);
		     Natives.writealarmsuspension(4, wa);
		    }
		tk.glucodata.SuperGattCallback.glucosealarms.sensorinit();
		} catch(Throwable e) {
			Log.stack(LOG_ID,"parseShort",e);
                	Applic.argToaster(context,context.getString(R.string.cantsetminutes)+str,Toast.LENGTH_SHORT);
			return;
			}
		}
            boolean haslow=((CheckBox) lowalarm[0]).isChecked();
            boolean hashigh=((CheckBox) highalarm[0]).isChecked();
           Natives.setalarms(str2float(((EditText)lowalarm[1]).getText().toString()),
                    str2float(((EditText)highalarm[1]).getText().toString()),
                     haslow, hashigh, isvalue.isChecked(),hasloss);
	Natives.setUSEALARM(usealarm.isChecked());
	   context.poponback();
		parview.setVisibility(VISIBLE);
		tk.glucodata.help.hidekeyboard(context);
		removeContentView(lay) ;
		issaved[0]=true;
	    });
	ringisvalue.setOnClickListener(v->{
		new tk.glucodata.RingTones(2).mkviews(context,"Value notification", lay);
		});
	ringlossalarm.setOnClickListener(v->{
		new tk.glucodata.RingTones(4).mkviews(context,"Loss of signal",lay);
		});
}


final private static String  codestr=String.valueOf(BuildConfig.VERSION_CODE);


//static private final List<String> supportedlanguages= Arrays.asList("Language","be","de","en","fr","it","nl","pl","pt","sv","uk","zh");
//  static private final List<String> supportedlanguages= Arrays.asList("Language","be","de","en","fr","it","nl","pl","pt","sv","uk");
//static private final List<String> supportedlanguages= Arrays.asList("Language","be","de","en","fr","it","nl","pl","pt");
static private final List<String> supportedlanguages= RU?Arrays.asList("Language","be","de","en","fr","it","nl","pl","pt","ru","sv","uk","zh"):(SPANISH?Arrays.asList("Language","be","de","en","es","fr","it","nl","pl","pt","sv","uk","zh"):Arrays.asList("Language","be","de","en","fr","it","nl","pl","pt","sv","uk","zh"));

//static private final List<String> supportedlanguages= IWRU?Arrays.asList("Language","be","de","en","es","fr","it","iw","nl","pl","pt","ru","sv","uk"):Arrays.asList("Language","be","de","en","es","fr","it","nl","pl","pt","sv","uk");
static public Spinner getGenSpin(Activity context) {
   var spin=  new Spinner(context, MODE_DROPDOWN);
   if(isWearable) {
      spin.setPopupBackgroundResource(R.drawable.helpbackground);
   //   spin.setPopupBackgroundResource(BLACK);
//      spin.setDropDownVerticalOffset(0);
      var width= GlucoseCurve.getwidth();
      spin.setDropDownWidth(width);
      spin.setDropDownHorizontalOffset(0);
      }
     return spin;
     }

static private Spinner languagespinner(MainActivity context) {
//	var spin=  new Spinner(context,isWearable?MODE_DIALOG: MODE_DROPDOWN);
   var spin=  getGenSpin(context);
   if(isWearable) {
      spin.setDropDownVerticalOffset((int)(GlucoseCurve.getheight()*.24));
      }
	var locales=AppCompatDelegate.getApplicationLocales();
	int prepos;
	if(locales.isEmpty()||(prepos=supportedlanguages.indexOf(locales.get(0).getLanguage()))<1)
		prepos=0;
	final int pos=prepos;
	spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		@Override
		public  void onItemSelected (AdapterView<?> parent, View view, int position, long id) {
			if(position!=pos) {
		             var newlocale=(position==0)?getEmptyLocaleList():LocaleListCompat.forLanguageTags(supportedlanguages.get(position));
			   AppCompatDelegate.setApplicationLocales(newlocale);
			   }
			}
		@Override
		public  void onNothingSelected (AdapterView<?> parent) {

		} });

	avoidSpinnerDropdownFocus(spin);
	supportedlanguages.set(0,context.getString(R.string.languagename));
   final var adapt=new LabelAdapter<String>(context,supportedlanguages,0);
	spin.setAdapter(adapt);

//	var pos=supportedlanguages.indexOf(getlocale().getLanguage()); if(pos<0) pos=0;
	spin.setSelection(pos);

//	   spin.setPadding(0,0,0,0);
	return spin;
	}
static void mkrangelabel(TextView view,int res,float low, float high) {
     view.setText(Applic.app.getString(res)+" "+float2string(low)+"-"+float2string(high));
   }
static private void displaysettings(MainActivity context,Settings settings) {
/*
     var graphrange=new RangeSlider(context);
	 var unit=Natives.getunit();
	 var maxvalue=unit==1?27.8f:500.0f;
     final float roundon=unit==1?10.0f:1.0f;
     float glow= round(Natives.graphlow(),roundon);
     float ghigh= round(Natives.graphhigh(),roundon);
     graphrange.setValueFrom(0);
     final var maxgraph=unit==1?21.0f:378.0f;
     graphrange.setValueTo(maxgraph);
     if(unit==1) {
        graphrange.setStepSize(0.1f);
        }
     else {
        graphrange.setStepSize(1.0f);
        }
     if(ghigh>maxgraph)
      ghigh=maxgraph;
      if(glow>ghigh)
         glow=ghigh;
     graphrange.setValues(glow,ghigh);
     graphrange.setTickVisible(false);
     TextView graphlabel = new TextView(context);
     mkrangelabel(graphlabel,R.string.graphrange,glow,ghigh);


       targetrange.setStepSize(0.1f);
        targetrange.setStepSize(1.0f);
     final var maxtarget=unit==1?10.0f:180.0f;
     var targetrange=new RangeSlider(context);
     targetrange.setTickVisible(false);
     targetrange.setValueFrom(0);
     targetrange.setValueTo(maxtarget);
      float tlow=round(Natives.targetlow(),roundon);
      float thigh=round(Natives.targethigh(),roundon);
     if(thigh>maxtarget)
      thigh=maxtarget;
      if(tlow>thigh)
         tlow=thigh;
     targetrange.setValues(tlow,thigh);
    TextView targetlabel = new TextView(context);
     mkrangelabel(targetlabel,R.string.targetrange,tlow,thigh);
*/

        TextView graphlabel = new TextView(context);
        graphlabel.setText(R.string.graphrange);
        var glow = new EditText(context);
        glow.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        glow.setImeOptions(editoptions);
        glow.setMinEms(1);


        TextView line = new TextView(context);
        line.setText("-");
        var ghigh = new EditText(context);

        ghigh.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        ghigh.setImeOptions(editoptions);
        ghigh.setMinEms(2);
        glow.setText(float2string(Natives.graphlow()));
        ghigh.setText(float2string(Natives.graphhigh()));
        View[] graphrow = {graphlabel, glow, line, ghigh};

	TextView targetlabel = getlabel(context,R.string.targetrange);
        var tlow = new EditText(context);

        tlow.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        tlow.setMinEms(1);
        tlow.setImeOptions(editoptions);
        TextView line2=new TextView(context); line2.setText("-");
        var thigh = new EditText(context);

        thigh.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        thigh.setMinEms(2);
        thigh.setImeOptions(editoptions);
        View[] targetrow = {targetlabel, tlow, line2, thigh};



        tlow.setText(float2string(Natives.targetlow()));
        thigh.setText(float2string(Natives.targethigh()));

	CheckBox levelleft= new CheckBox(context);
	levelleft.setText(R.string.glucoseaxisleft);
	levelleft.setChecked(Natives.getlevelleft());

    levelleft.setOnCheckedChangeListener( (buttonView,  isChecked) -> {
	         Natives.setlevelleft(isChecked);
            });

	var colbut=getbutton(context,R.string.colors);
   var help=getbutton(context,R.string.helpname);
  help.setOnClickListener(v->{help(R.string.displayhelp,context); });

    var hour12=getcheckbox(context,R.string.hour12,!Natives.gethour24());
    hour12.setOnCheckedChangeListener( (buttonView,  isChecked) -> {
         Applic.sethour24(!isChecked);
         });
    var fixed=getcheckbox(context,R.string.clampnow,Natives.getcurrentRelative());
	 fixed.setOnCheckedChangeListener( (buttonView,  isChecked) -> Natives.setcurrentRelative(isChecked));


      /*
      graphrange.addOnSliderTouchListener(
         new RangeSlider.OnSliderTouchListener() {
			 @Override
			 public void onStartTrackingTouch(@NonNull RangeSlider slider) {
				 Log.i(LOG_ID,"graph onStartTrackingTouch");

			 }

			 @Override
			 public void onStopTrackingTouch(@NonNull RangeSlider slider) {
				 final var values=slider.getValues();
				 final var low=values.get(0);
				 final var high=values.get(1);
            mkrangelabel(graphlabel,R.string.graphrange,low,high);
				 Log.i(LOG_ID,"stop graphrange "+low+" - "+high);
				 Natives.setGraphRange(low,high);

			 }

         });
      targetrange.addOnSliderTouchListener(
         new RangeSlider.OnSliderTouchListener() {
           @Override
           public void onStartTrackingTouch(RangeSlider slider) {
				Log.i(LOG_ID,"target onStartTrackingTouch");
           }

           @Override
           public void onStopTrackingTouch(RangeSlider slider) {
               final var values=slider.getValues();
               final var low=values.get(0);
               final var high=values.get(1);
               mkrangelabel(targetlabel,R.string.targetrange,low,high);
               Log.i(LOG_ID,"stop targetrange "+low+" - "+high);
               Natives.setTargetRange(low,high);
           }
         });
*/
	TextView scalelabel=getlabel(context,R.string.manuallyscale);
	CheckBox fixatex =new CheckBox(context);

	CheckBox fixatey =new CheckBox(context);


	fixatex.setText(R.string.time);
	fixatey.setText(R.string.glucose);
	fixatex.setChecked(!Natives.getfixatex());
	fixatey.setChecked(!Natives.getfixatey());

		final var threslabel=getlabel(context,R.string.threshold);

		final var thresstring=float2string(Natives.getthreshold());
		final var threshold=getnumedit(context,thresstring);

      
      var close=getbutton(context,R.string.closename);

	var langspin=languagespinner(context);
      Layout lay;
    if(isWearable)  {
      if(!useclose)
          close.setVisibility(GONE);
        targetlabel.setPadding((int)(tk.glucodata.GlucoseCurve.metrics.density*5.0),0,0,0);
        graphlabel.setPadding((int)(tk.glucodata.GlucoseCurve.metrics.density*5.0),0,0,0);
        colbut.setPadding(0,0,0,0);
        threslabel.setPadding((int)(tk.glucodata.GlucoseCurve.metrics.density*7.0),0,0,0);
   //      Button display=getbutton(context,context.getString(R.string.display));
 	var Scans=getcheckbox(context,R.string.scansname,getshowscans()) ;
 	var History=getcheckbox(context,R.string.historyname,getshowhistories()) ;
 	var Stream=getcheckbox(context,R.string.streamname,getshowstream()) ;
 	var Amounts=getcheckbox(context,R.string.amountshort,getshownumbers()) ;
 	var setuseclose=getcheckbox(context,R.string.useclose,useclose) ;
    setuseclose.setOnCheckedChangeListener( (buttonView,  isChecked) -> { 
         Specific.setclose(isChecked);
         Natives.setdontuseclose(!isChecked); 
         context.finish();
         context.startActivity(context.getIntent());
      });

Scans.setOnCheckedChangeListener( (buttonView,  isChecked) -> { Natives.setshowscans(isChecked); });
	History.setOnCheckedChangeListener( (buttonView,  isChecked) -> { Natives.setshowhistories(isChecked); });
	Stream.setOnCheckedChangeListener( (buttonView,  isChecked) -> { Natives.setshowstream(isChecked); });
	Amounts.setOnCheckedChangeListener( (buttonView,  isChecked) -> { Natives.setshownumbers(isChecked); });
   var space=getlabel(context,"");
	   fixed.setPadding(0,0,0,(int)(tk.glucodata.GlucoseCurve.metrics.density*7.0));
        lay = new Layout(context, (l, w, h) -> {
    		      int[] ret={w,h};
		         return ret;
               },new View[]{colbut},graphrow,targetrow,new View[] {hour12},new View[]{space},new View[]{scalelabel}, new View[]{fixatex},new View[]{fixatey},new View[]{threslabel,threshold},new View[] {levelleft},new View[]{fixed},new View[]{Scans},new View[]{History},new View[]{Stream},new View[]{Amounts},new View[]{setuseclose},new View[]{close},new View[]{langspin});
         }
      else {	
      var iob=getcheckbox(context,"IOB",Natives.getIOB());
        var dexfuture=getcheckbox(context,R.string.dexfuture,Natives.getdexcomPredict());
         dexfuture.setOnCheckedChangeListener( (buttonView,  isChecked) -> Natives.setdexcomPredict(isChecked) );
	      CheckBox reverseorientation =getcheckbox(context,R.string.invertscreen,(Natives.getScreenOrientation()&SCREEN_ORIENTATION_REVERSE_LANDSCAPE)!=0);
         reverseorientation.setOnCheckedChangeListener( (buttonView,  isChecked) ->  {
				int ori= (isChecked?SCREEN_ORIENTATION_REVERSE_LANDSCAPE:SCREEN_ORIENTATION_LANDSCAPE);
				Natives.setScreenOrientation(ori);
				});
        lay = new Layout(context, (l, w, h) -> {
    		      int[] ret={w,h};
		         return ret;
               },graphrow,new View[]{scalelabel,fixatex, fixatey},targetrow,new View[]{threslabel,threshold,dexfuture},new View[] {levelleft,reverseorientation},new View[] {hour12,langspin,iob,fixed},new View[]{colbut,help,close});

		iob.setOnCheckedChangeListener( (buttonView,  isChecked) -> {
				if(!Natives.setIOB(isChecked)) {
					iob.setChecked(false);
					EnableControls(lay,false);
					tk.glucodata.help.help(R.string.IOB,context,l->EnableControls(lay,true) );
					}
				}
			);
         }

     lay.setBackgroundColor(Applic.backgroundcolor);
	if(isWearable) {
      final   int pad=(int)(tk.glucodata.GlucoseCurve.metrics.density*10.0);
	   lay.setPadding((int)(tk.glucodata.GlucoseCurve.metrics.density*5.0),(int)(tk.glucodata.GlucoseCurve.metrics.density*11.0),(int)(tk.glucodata.GlucoseCurve.metrics.density*15.0),pad*2);
		}
     else {
      final   int pad=(int)(tk.glucodata.GlucoseCurve.metrics.density*8.0);
	   lay.setPadding(MainActivity.systembarLeft+pad,MainActivity.systembarTop,pad+MainActivity.systembarRight,pad+MainActivity.systembarBottom);
      }

	var scroll=new ScrollView(context);
	scroll.addView(lay);
	scroll.setFillViewport(true);
	scroll.setSmoothScrollingEnabled(false);
   scroll.setScrollbarFadingEnabled(true);
   scroll.setVerticalScrollBarEnabled(Applic.scrollbar);
   context.addContentView(scroll, new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
   colbut.setOnClickListener(v-> {
   		MainActivity.doonback();
		settings.finish();
		SetColors.show(context);
		});

Runnable closerun= () -> {
      var newthreshold=threshold.getText().toString();
      if(!thresstring.equals(newthreshold)) {
	    float thres=str2float(newthreshold);
	    if(thres>0.8f||thres<0.0f) {
		  Applic.argToaster(context, "A threshold should 0.0 - 0.8",Toast.LENGTH_LONG);
	       }
	     else
	       setthreshold(thres);
	    }
		Natives.setfixatex(!fixatex.isChecked());
		Natives.setfixatey(!fixatey.isChecked());
       Natives.setGraphRange(str2float(glow.getText().toString()), str2float(ghigh.getText().toString()));
      Natives.setTargetRange(str2float(tlow.getText().toString()), str2float(thigh.getText().toString()));
		removeContentView(scroll) ;
		};
	MainActivity.setonback(closerun);

      close.setOnClickListener(v->{
		   context.poponback();
         closerun.run();
      });

   }
       /*graphrange.setLabelFormatter(f->{
            var str= Settings.float2string(f);
          Log.i(LOG_ID,"setLabelformatter "+str);
          return str;}); */
//	   graphrange.setLabelBehavior(LabelFormatter.LABEL_WITHIN_BOUNDS);
//	   graphrange.setLabelBehavior(LabelFormatter.LABEL_FLOATING);
 //      graphrange.setHaloRadius((int)(tk.glucodata.GlucoseCurve.metrics.density*15.0f));
private	void mksettings(MainActivity context,boolean[] issaved) {

    if(settinglayout==null) {
            TextView unitlabel = new TextView(context);
        unitlabel.setText(R.string.unit);
        mmolL = new RadioButton(context);

        mmolL.setOnClickListener(v-> {
         ((Applic) context.getApplication()).setunit(1);
                  mgdl.setChecked(false);
                 recreate(issaved);
                });

            mmolL.setText(R.string.mmolL);
         mgdl = new RadioButton(context);
        mgdl.setOnClickListener(v-> {
            ((Applic) context.getApplication()).setunit(2);
            mmolL.setChecked(false);
              recreate(issaved);
           });

        mgdl.setText(R.string.mgdL);

	     final   int padmg=(int)(tk.glucodata.GlucoseCurve.metrics.density*8.0);
        mgdl.setPadding(0,0,padmg,0);
        mgdl.setPadding(0,0,0,0);
        mmolL.setPadding(0,0,0,0);
         var leftspace=new Space(context);
        View[] row0 = isWearable?new View[]{leftspace,mmolL, mgdl,new Space(context)}:new View[]{unitlabel, mmolL, mgdl};

        Button changelabels=new Button(context);
        Button help =new Button(context);
        help.setText(R.string.helpname);
        help.setOnClickListener(v->{help(R.string.settinghelp,(MainActivity)(v.getContext())); });

      var close=getbutton(context,R.string.closename);

      if(!useclose)
	 close.setVisibility(GONE);
//	CheckBox bluetooth= new CheckBox(context);
   CheckBox globalscan = new CheckBox(context);
	globalscan.setText(R.string.startsapp);

	final var hasnfc=MainActivity.hasnfc;
      final  CheckBox nfcsound=hasnfc?new CheckBox(context):null;
	if(hasnfc)  {
		nfcsound.setText(R.string.nfcsound);
		nfcsound.setChecked(Natives.nfcsound());
		scanenabled=IntentscanEnabled();
		globalscan.setChecked(scanenabled) ; //Value
		globalscan.setOnCheckedChangeListener( (buttonView,  isChecked) -> 
			    EnableIntentScanning(isChecked));
	       nfcsound.setOnCheckedChangeListener((buttonView,  isChecked) -> {
				Natives.setnfcsound(isChecked);
				context.setnfc();
				});
		}
	CheckBox camera=!isWearable?new CheckBox(context):null;
	if(!isWearable) {
		final int diskey=!isWearable?Natives.camerakey():0;
		if(diskey>0)  {
			camera.setText(R.string.disablecamerakey);
			camera.setChecked(diskey==1);
			camera.setOnCheckedChangeListener((buttonView,  isChecked) -> {
				 final int setto = isChecked ? 1 : 2;
				 Natives.setcamerakey(setto);
				 });

			}
			
		else
			camera.setVisibility(GONE);

		}

   var exchanges=getbutton(context,R.string.exchanges);
	final boolean blueused=Natives.getusebluetooth();

	//bluetooth.setChecked(blueused);
	var alarmbut=getbutton(context,R.string.alarms);
        alarmbut.setOnClickListener(v->{
		alarmsettings(context,settinglayout,issaved);
		});



    close.setOnClickListener(v->{
		    int unit=mmolL.isChecked()?1:(mgdl.isChecked()?2:0);
		    if(unit==0) {
			Applic.argToaster(context, R.string.setunitfirst,Toast.LENGTH_SHORT);
		   	return;
		    }
		   context.poponback();
		    hidekeyboard();
		    finish();
	 context.lightBars(!getInvertColors( ));
	     if(tk.glucodata.Menus.on) tk.glucodata.Menus.show(context);

			});
   var displayview=getbutton(context,R.string.display);
	Layout[] thelayout=new Layout[1];
	if(!isWearable) {
		changelabels.setText(R.string.numberlabels);
		changelabels.setOnClickListener(v-> {
			    hidekeyboard();
			    new LabelsClass(context).mklabellayout(thelayout[0]);});
			  }
	Button numalarm=getbutton(context,R.string.remindersname);
	Button advanced=null;


	View[][] views;
	final String advhelp=isWearable?null:Natives.advanced();
	if(isWearable) {
		Button complications;
		if(BuildConfig.minSDK>=26) {
			complications = getbutton(context, R.string.complications);
			complications.setOnClickListener(v -> tk.glucodata.glucosecomplication.ColorConfig.show(context, thelayout[0]));
		}

      alarmbut.setMinimumWidth(0);
      alarmbut.setMinWidth(0);
		var uppad=(int)(tk.glucodata.GlucoseCurve.metrics.density*9.0);
      alarmbut.setPadding(uppad,alarmbut.getPaddingTop(),uppad,alarmbut.getPaddingBottom());

      var floatconfig=getbutton(context,R.string.floatglucoseshort);

      floatconfig.setOnClickListener(v-> tk.glucodata.FloatingConfig.show(context,thelayout[0]));
		CheckBox floatglucose=new CheckBox(context);
		floatglucose.setText("   " );


		floatglucose.setChecked(Natives.getfloatglucose());
		floatglucose.setOnCheckedChangeListener( (buttonView,  isChecked) -> Floating.setfloatglucose(context,isChecked) ) ;
//      var space=getlabel(context,"");
 //     var space2=getlabel(context,"");
		View[] camornum=new View[] {alarmbut,numalarm};
		if(BuildConfig.minSDK>=26) {
			views = new View[][]{new View[]{displayview},row0, hasnfc ? (new View[]{globalscan, nfcsound}) : null, new View[]{exchanges },new View[]{complications},  new View[]{floatconfig, floatglucose}, camornum, new View[]{close}, new View[]{getlabel(context, BuildConfig.BUILD_TIME)}, new View[]{getlabel(context, BuildConfig.VERSION_NAME)}, new View[]{getlabel(context, codestr)}};
			;
		}
		else{
			views = new View[][]{new View[]{displayview},row0,    hasnfc ? (new View[]{globalscan, nfcsound}) : null,  new View[]{exchanges },   new View[]{floatconfig, floatglucose}, camornum,new View[]{close},  new View[]{getlabel(context, BuildConfig.BUILD_TIME)}, new View[]{getlabel(context, BuildConfig.VERSION_NAME)}, new View[]{getlabel(context, codestr)}};
			;
		}
		}
	else {
		View[] row9;
		var about=getbutton(context,R.string.aboutname);
	       about.setOnClickListener(v-> tk.glucodata.GlucoseCurve.doabout(context));
		if(advhelp!=null) {
			advanced=new Button(context);
			advanced.setText(R.string.advanced);
			row9= new View[]{help,advanced,about,close} ;
			}
		else {
			row9= new View[]{help,about,close} ;
			}

//      var oldxdrip=getbutton(context,"send old"); oldxdrip.setOnClickListener(v-> tk.glucodata.Natives.sendxdripold());
      CheckBox glucosenotify=new CheckBox(context);
		glucosenotify.setText(R.string.glucosestatusbar);
		glucosenotify.setChecked(Natives.getshowalways()) ;
		glucosenotify.setOnCheckedChangeListener( (buttonView,  isChecked) -> Notify.glucosestatus(isChecked) );
      var floatconfig=getbutton(context,R.string.floatglucose);


	    floatconfig.setOnClickListener(v-> tk.glucodata.FloatingConfig.show(context,thelayout[0]));
		View[] rowglu=new View[]{floatconfig,glucosenotify};
		views=new View[][]{row0, hasnfc?new View[]{nfcsound, globalscan,camera}:null,rowglu,new View[]{exchanges,numalarm,alarmbut},new View[]{changelabels,displayview}, row9};
		}

	help.setFocusableInTouchMode(true);
	help.setFocusable(true);
	 help.requestFocus();
	 help.requestFocusFromTouch();

        Layout lay = new Layout(context, (l, w, h) -> {
    		hideSystemUI(); int[] ret={w,h};
		
		return ret;
		},views);

     exchanges.setOnClickListener(v->{
        exchanges(context,lay);
        });
	thelayout[0]=lay;
		if(advhelp!=null) {
			advanced.setOnClickListener(v -> {
				EnableControls(thelayout[0],false);
				help(advhelp, (MainActivity) (v.getContext()),l->
					EnableControls(thelayout[0],true)
					
					);
			});
			}


        lay.setBackgroundColor(colorwindowbackground);
/*var	horlayout= new HorizontalScrollView(context);
	horlayout.addView(lay);
	horlayout.setHorizontalScrollBarEnabled(false);
	horlayout.setFillViewport(true);
   horlayout.setPadding(0,0,0,0); */

	ScrollView scroller=new ScrollView(context);
	scroller.addView(lay);
	scroller.setSmoothScrollingEnabled(false);
   scroller.setVerticalScrollBarEnabled(Applic.scrollbar);
   scroller.setScrollbarFadingEnabled(true);//Crash with NestedScrollView
	scroller.setFillViewport(true);
	scroller.setPadding(0,0,0,0);

   settinglayout=scroller;


	   final   int pad=(int)(tk.glucodata.GlucoseCurve.metrics.density*7.0);
	if(isWearable) {
	   lay.setPadding((int)(tk.glucodata.GlucoseCurve.metrics.density*3.0),(int)(tk.glucodata.GlucoseCurve.metrics.density*11.0),pad,pad);
		}
     else {
	   lay.setPadding(MainActivity.systembarLeft+pad,MainActivity.systembarTop,pad+MainActivity.systembarRight,pad+MainActivity.systembarBottom);
      }

	final	int laywidth=MATCH_PARENT;
	 context.addContentView(settinglayout, new ViewGroup.LayoutParams( laywidth ,MATCH_PARENT));
	numalarm.setOnClickListener(v-> {
		new tk.glucodata.setNumAlarm().mkviews(context,settinglayout);
		});
    displayview.setOnClickListener(v-> {
	 displaysettings(context,this);
	 });
        }
    else {

        settinglayout.setVisibility(VISIBLE);

	settinglayout.bringToFront();
    }

setvalues();
}

static private void exchanges(MainActivity context,View parent) {
  parent.setVisibility(GONE);
	final CheckBox xdripbroadcast = new CheckBox(context);
	final CheckBox jugglucobroadcast = new CheckBox(context);

   if(isWearable)
      xdripbroadcast.setText("xDrip broadcast");
   else
      xdripbroadcast.setText(R.string.xdripbroadcast);
	xdripbroadcast.setChecked(Natives.getxbroadcast());
   if(isWearable)
      jugglucobroadcast.setText("Glucodata");
   else
      jugglucobroadcast.setText("Glucodata broadcast");
	jugglucobroadcast.setChecked(Natives.getJugglucobroadcast());
   var mirrorview=getbutton(context,R.string.mirror);
   mirrorview.setOnClickListener(v ->{ (new Backup()).realmkbackupview(context,false); });
	Layout[] thelayout = {null};
		final boolean[] xbroadnothing = {false};
		xdripbroadcast.setOnCheckedChangeListener((buttonView, isChecked) -> {
					if (!xbroadnothing[0]) {
						xbroadnothing[0] = true;
						xdripbroadcast.setChecked(!isChecked);
						Broadcasts.setxdripreceivers(context, thelayout[0], xdripbroadcast, xbroadnothing);
					}
				}
		);

		final boolean[] juggluconothing = {false};
		jugglucobroadcast.setOnCheckedChangeListener((buttonView, isChecked) -> {
					if (!juggluconothing[0]) {
						juggluconothing[0] = true;
						jugglucobroadcast.setChecked(!isChecked);
						Broadcasts.setglucodatareceivers(context, thelayout[0], jugglucobroadcast, juggluconothing);
					}
				}
		);
   var ok = getbutton(context, R.string.closename);
	if(!useclose)
		ok.setVisibility(GONE);
	ok.setOnClickListener(
		v->{
	      context.doonback();
		});
   Layout lay;
	if (isWearable) {
		var uploader = getbutton(context, R.string.upload);
		uploader.setOnClickListener(v -> tk.glucodata.NightPost.config(context, thelayout[0]));
		uploader.setMinimumWidth(0);
		uploader.setMinWidth(0);
		//        var uppad=(int)(tk.glucodata.GlucoseCurve.metrics.density*9.0);
//          uploader.setPadding(uppad,alarmbut.getPaddingTop(),uppad,alarmbut.getPaddingBottom());

		lay = new Layout(context, (l, w, h) -> {
			int[] ret = {w, h};
			return ret;
		},new View[]{xdripbroadcast},new View[]{uploader,mirrorview}  ,new View[]{jugglucobroadcast}, new View[]{ok});

   final var density=tk.glucodata.GlucoseCurve.metrics.density;
		lay.setPadding((int)(density*8.0),(int)(density*20.0),(int)(density*8.0),(int)(density*2.0));
	} else {
		var uploader = getbutton(context, R.string.uploader);
		uploader.setOnClickListener(v -> tk.glucodata.NightPost.config(context, thelayout[0]));
		final CheckBox librelinkbroadcast = new CheckBox(context);
		final CheckBox libreview = new CheckBox(context);
		final CheckBox everSensebroadcast = new CheckBox(context);
		final var healthconnect = (isWearable || Build.VERSION.SDK_INT < 28) ? null : getcheckbox(context, "Health Connect", Natives.gethealthConnect());
		final boolean wasxdrip = Natives.getuselibreview();
		final boolean usedlibrebroad = Natives.getlibrelinkused();
		libreview.setText(R.string.libreviewname);
		libreview.setChecked(wasxdrip);
		librelinkbroadcast.setText(R.string.sendtoxdrip);
		librelinkbroadcast.setChecked(usedlibrebroad);
		everSensebroadcast.setText(R.string.everSensebroadcast);
		everSensebroadcast.setChecked(Natives.geteverSensebroadcast());
		if (Build.VERSION.SDK_INT >= 28) {
			healthconnect.setOnCheckedChangeListener((buttonView, isChecked) -> {
						Natives.sethealthConnect(isChecked);
						if (isChecked) {
							MainActivity.tryHealth = 5;
							HealthConnection.Companion.init(context);
						} else {
							MainActivity.tryHealth = 0;
							HealthConnection.Companion.stop();
						}
					}
			);
		}
		if (librelinkbroadcast.isChecked() != usedlibrebroad) {
			if (!usedlibrebroad) {
				final var starttime = Natives.laststarttime();
				if (starttime != 0L) {
					tk.glucodata.XInfuus.sendSensorActivateBroadcast(context, Natives.lastsensorname(), starttime);
				}
			}
		}
		var webserver = getbutton(context, R.string.webserver);
	/*
		final View[] librerow=
		 (Build.VERSION.SDK_INT >= 28)? 
			new View[]{glucosenotify,healthconnect,libreview}
			:
			new View[]{glucosenotify,libreview}; */
		webserver.setOnClickListener(v -> tk.glucodata.Nightscout.show(context, thelayout[0]));
		uploader.setOnClickListener(v -> tk.glucodata.NightPost.config(context, thelayout[0]));
		final boolean[] donothing = {false};
		libreview.setOnCheckedChangeListener(
				(buttonView, isChecked) -> {
					if (!donothing[0]) {
						donothing[0] = true;
						libreview.setChecked(!isChecked);
						Libreview.config(context, thelayout[0], libreview, donothing);
					}
				});
		final boolean[] xdripdonthing = {false};
		librelinkbroadcast.setOnCheckedChangeListener(
				(buttonView, isChecked) -> {
					if (!xdripdonthing[0]) {
						xdripdonthing[0] = true;
						librelinkbroadcast.setChecked(!isChecked);
						Broadcasts.setlibrereceivers(context, thelayout[0], librelinkbroadcast, xdripdonthing);
					}
				});

		final boolean[] everSensenothing = {false};
		everSensebroadcast.setOnCheckedChangeListener((buttonView, isChecked) -> {
					if (!everSensenothing[0]) {
						everSensenothing[0] = true;
						everSensebroadcast.setChecked(!isChecked);
						Broadcasts.seteverSensereceivers(context, thelayout[0], everSensebroadcast, everSensenothing);
					}
				}
		);
		var help = getbutton(context, R.string.helpname);
      help.setOnClickListener(v->{help(R.string.exchangehelp,context); });
      var exportview=getbutton(context,R.string.export);

		lay = new Layout(context, (l, w, h) -> {
			int[] ret = {w, h};
			return ret;
		}, new View[]{librelinkbroadcast, everSensebroadcast},new View[]{xdripbroadcast, jugglucobroadcast}, new View[]{webserver, uploader, libreview}, (Build.VERSION.SDK_INT >= 28) ? new View[]{healthconnect,exportview,mirrorview} :new View[]{exportview,mirrorview},
				new View[]{help, ok});

	final   int pad=(int)(tk.glucodata.GlucoseCurve.metrics.density*10.0);
		lay.setPadding(MainActivity.systembarLeft,MainActivity.systembarTop,MainActivity.systembarRight+pad,MainActivity.systembarBottom);
		exportview.setOnClickListener(v ->{
			var c=Applic.app.curve;
			if(c!=null) {
				c.dialogs.showexport(context,c.getWidth(),c.getHeight(),lay);
			}
		});
      }

	thelayout[0] = lay;
    lay.setBackgroundColor(Applic.backgroundcolor);
	   context.addContentView(lay, new ViewGroup.LayoutParams( MATCH_PARENT ,MATCH_PARENT));

	context.setonback(() -> {
     parent.setVisibility(VISIBLE);
		removeContentView(lay) ;
		});
}


//ViewGroup labellayout=null;



public static void   removeContentView(View view) {
    ViewGroup parent= (ViewGroup)view.getParent();
    if(parent!=null)
        parent.removeView(view);
    }

//@Override

}
