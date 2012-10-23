#include <sndfile.h>
#include "Object.h"
#include "SoundFileNative.h"

/*
 * Class:     org_emmef_sndfile_SoundFileType
 * Method:    initLibSoundFile
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_org_emmef_sndfile_SoundFileType_initLibSoundFile
		(JNIEnv *env, jclass) {
	return initExceptions(env);
}
/*
 * Class:     org_emmef_sndfile_SoundFile
 * Method:    checkSoundFileType
 * Signature: (III)Z
 */
JNIEXPORT jboolean JNICALL Java_org_emmef_sndfile_SoundFileType_isValidFormat0(JNIEnv *env, jclass theClass, jint samplerate, jint format, jint channels) {
	try {
		SF_INFO info;
		info.channels = channels;
		info.samplerate = samplerate;
		info.format = format;
		return sf_format_check(&info) ? JNI_TRUE : JNI_FALSE;
	}
	catch (...) {
	}
	
	return JNI_FALSE;
	
}

/*
 * Class:     org_emmef_sndfile_SoundFile
 * Method:    getSndFileVersion0
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_emmef_sndfile_SoundFileType_getSndFileVersion0(JNIEnv *env, jclass) {
	initExceptions(env);
	try {
		int size= 4;
		char *walk = 0;
		bool doContinue = true;
		
		while (doContinue) {
			if (walk != 0) {
				delete walk;
			}
			walk = new char[size];
			
			sf_command(0, SFC_GET_LIB_VERSION, walk, size);
			
			int length = 0;
			while (length < size && walk[length] != '\0') {
				length++;
			}
			if ((size < 1024) && (length == 0 || length + 1 >= size)) {
				size *= 2;
			}
			else {
				doContinue = false;
			}
		}
		
		jstring version = env->NewStringUTF(walk);
		delete walk;
		
		return version;
	}
	catch (...) {
	}
	return 0;
}
  
/*
 * Class:     org_emmef_sndfile_SoundFile
 * Method:    getEncodingFormatCount0
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_emmef_sndfile_SoundFileType_getSimpleFormatCount0(JNIEnv *, jclass) {
	int result = -1;
	
	sf_command(0, SFC_GET_SIMPLE_FORMAT_COUNT, &result, sizeof(result));
	
	return result;
}

/*
 * Class:     org_emmef_sndfile_SoundFile
 * Method:    getEncodingFormatInfo0
 * Signature: (ILorg/emmef/sndfile/SoundFile$FormatInfo;)Z
 */
JNIEXPORT jboolean JNICALL Java_org_emmef_sndfile_SoundFileType_getSimpleFormatInfo0(JNIEnv *env, jclass, jint index, jobject infoObject) {
	Object object(env, infoObject);
	SF_FORMAT_INFO info;
	info.format = index;
	if (sf_command(0, SFC_GET_SIMPLE_FORMAT, &info, sizeof(info)) == 0) {
		object.setInt("format", info.format);
		object.setString("name", info.name);
		object.setString("extension", info.extension);
		return JNI_TRUE;
	}
	else {
		return JNI_FALSE;
	}
}

/*
 * Class:     org_emmef_sndfile_SoundFile
 * Method:    getMajorFormatCount
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_emmef_sndfile_SoundFileType_getMajorFormatCount0(JNIEnv *, jclass) {
	int result = -1;
	
	sf_command(0, SFC_GET_FORMAT_MAJOR_COUNT, &result, sizeof(result));
	
	return result;
}

/*
 * Class:     org_emmef_sndfile_SoundFile
 * Method:    getMajorFormatInfo0
 * Signature: (ILorg/emmef/sndfile/SoundFile$FormatInfo;)Z
 */
JNIEXPORT jboolean JNICALL Java_org_emmef_sndfile_SoundFileType_getMajorFormatInfo0(JNIEnv *env, jclass, jint index, jobject infoObject) {
	Object object(env, infoObject);
	SF_FORMAT_INFO info;
	info.format = index;
	if (sf_command(0, SFC_GET_FORMAT_MAJOR, &info, sizeof(info)) == 0) {
		object.setInt("format", info.format);
		object.setString("name", info.name);
		object.setString("extension", info.extension);
		return JNI_TRUE;
	}
	else {
		return JNI_FALSE;
	}
}

/*
 * Class:     org_emmef_sndfile_SoundFile
 * Method:    getEncodingFormatCount0
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_emmef_sndfile_SoundFileType_getSubFormatCount0(JNIEnv *, jclass) {
	int result = -1;
	
	sf_command(0, SFC_GET_FORMAT_SUBTYPE_COUNT, &result, sizeof(result));
	
	return result;
}

/*
 * Class:     org_emmef_sndfile_SoundFile
 * Method:    getEncodingFormatInfo0
 * Signature: (ILorg/emmef/sndfile/SoundFile$FormatInfo;)Z
 */
JNIEXPORT jboolean JNICALL Java_org_emmef_sndfile_SoundFileType_getSubFormatInfo0(JNIEnv *env, jclass, jint index, jobject infoObject) {
	Object object(env, infoObject);
	SF_FORMAT_INFO info;
	info.format = index;
	if (sf_command(0, SFC_GET_FORMAT_SUBTYPE, &info, sizeof(info)) == 0) {
		object.setInt("format", info.format);
		object.setString("name", info.name);
		object.setString("extension", info.extension);
		return JNI_TRUE;
	}
	else {
		return JNI_FALSE;
	}
}

