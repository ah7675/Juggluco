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
/*      Sun Apr 16 20:57:54 CEST 2023                                                 */


package tk.glucodata;

import android.app.Application;
import android.content.IntentFilter;

public class Specific {
	static void start(Application context) {
		watchdrip.set(Natives.getwatchdrip());
		SuperGattCallback.doGadgetbridge = Natives.getgadgetbridge();
	}

	static void splash(Object act) {
	}

	static void settext(String str) {
	}

	static void rmlayout() {
	}

	static void initScreen(Object act) {
	}

	static void blockedNum(Object act) {
	}

	static public final boolean useclose = true;

	static public  void setclose(boolean c) { }
};
