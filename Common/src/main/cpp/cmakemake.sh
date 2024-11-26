#s/\/home\/jka\/src\/android\/Librefree/$APPDIR/g
APPDIR=/home/jka/src/android/Glucodata
#ABI=armeabi-v7a
model=$4
ANVERSION=$3
ABI=$2
btype=$1

OUTPUTDIR=$5

#OUTPUTDIR=$APPDIR/Common/build/mij/$model/$ABI/$btype
CXXFLAGS="-std=c++26 -DAPPID=\\\"tk.glucodata\\\" -DAPPSUFFIX=\\\"\\\" -DRELEASEID=1  -DLIBRE3=1  -DDEXCOM=1 -DSIBIONICS=1 "
CFLAGS="-DAPPID=\\\"tk.glucodata\\\" -DAPPSUFFIX=\\\"\\\" -DRELEASEID=1  -DLIBRE3=1  -DDEXCOM=1 -DSIBIONICS=1 "
if test $model = "watch"
then
CXXFLAGS+=" -DWEAROS=1  -DUSEDIN=1 "

fi

if test $btype = "Debug"
then
 CXXFLAGS+=" -DRELEASEID=1 -DRELEASE=1 -DNORAWSTREAM=1   -DRELEASEDIR=1  -DSCANLOG=1 "
 CFLAGS+=" -DRELEASEID=1 -DRELEASE=1 -DNORAWSTREAM=1   -DRELEASEDIR=1  -DSCANLOG=1 "
 DEBUGOPT="-DDEBUG=ON"
else
CXXFLAGS+=" -DRELEASEID=1 -DRELEASE=1 -DNORAWSTREAM=1   -DRELEASEDIR=1  -DNOLOG=1 "
CFLAGS+=" -DRELEASEID=1 -DRELEASE=1 -DNORAWSTREAM=1   -DRELEASEDIR=1  -DNOLOG=1 "
 DEBUGOPT="-DDEBUG=OFF"
fi
echo $CXXFLAGS
cmake\
	-H$APPDIR/Common/src/main/cpp\
	$DEBUGOPT -DCMAKE_CXX_FLAGS="-std=c++2b $CXXFLAGS "\
	-DCMAKE_FIND_ROOT_PATH=$OUTPUTDIR\
	-DCMAKE_BUILD_TYPE=$btype\
	-DCMAKE_TOOLCHAIN_FILE=$ANDROID_NDK/build/cmake/android.toolchain.cmake\
	-DANDROID_ABI=$ABI\
	-DANDROID_NDK=$ANDROID_NDK\
	-DANDROID_PLATFORM=android-$ANVERSION\
	-DCMAKE_ANDROID_ARCH_ABI=$ABI\
	-DCMAKE_ANDROID_NDK=$ANDROID_NDK\
	-DCMAKE_EXPORT_COMPILE_COMMANDS=ON\
	-DCMAKE_LIBRARY_OUTPUT_DIRECTORY=$OUTPUTDIR\
	-DCMAKE_SYSTEM_NAME=Android\
	-DCMAKE_SYSTEM_VERSION=19\
	-DCMAKE_VERBOSE_MAKEFILE=1\
	-B$OUTPUTDIR\
	-G'Unix Makefiles'



