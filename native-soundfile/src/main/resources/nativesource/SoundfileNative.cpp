#include <string>
#include <iostream>
#include <sndfile.h>
#include <stdint.h>
#include "Exceptions.h"
#include "Object.h"
#include "JavaArray.h"
#include "SoundFileNative.h"


#ifdef __cplusplus
extern "C" {
#endif

static void assert(bool condition, JNIEnv *env) {
	if (condition) {
		const int errorCode = sf_error(NULL);
		const char * const msg = errorCode != 0 ? sf_error_number(errorCode) : 0;
		
		throw IOException(env, msg != 0 ? msg : "An exception occurred, but no message was available");
	}
}


static sf_count_t getFrameCount(JNIEnv *env, jint channels, jint length, jlong frames) {
	char message[128];
	if (channels < 0) {
		throw IllegalArgumentException(env, "getFrameCount(): number of channels is zero");
	}
	jlong bufferSize = frames;
	bufferSize *= channels;
	fflush(stdout);
	if ((jlong)length < bufferSize) {
		snprintf(message, 127, "getFrameCount(): Buffer of size %i cannot contain %zi frames of %i channels.", length, frames, channels);
		throw IllegalArgumentException(env, message);
	}
	
	return frames;
}

int convertWhence(JNIEnv *env, jobject whenceEnum) {
	char message[41];
	Object whenceObject(env, whenceEnum);
	const int javaWhence = whenceObject.getInt("value");
	switch (javaWhence) {
		case org_emmef_sndfile_SoundFile_WHENCE_SET : 
			return SEEK_SET;
		case org_emmef_sndfile_SoundFile_WHENCE_CURRENT :
			return SEEK_CUR;
		case org_emmef_sndfile_SoundFile_WHENCE_FROM_END : 
			return SEEK_END;
		default : 
			snprintf(message, 40, "Illegal seek constant %i", javaWhence);
			throw IllegalArgumentException(env, message);
	}
}

/*
 * Class:     org_emmef_sndfile_SoundFile
 * Method:    openReadable
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT jlong JNICALL Java_org_emmef_sndfile_SoundFile_openReadable
	  (JNIEnv *env, jclass type, jstring fileName, jobject infoObject) {
  try {
		JavaString guardedFileName(env, fileName);
		Object me(env, infoObject);
		SF_INFO info;
		SNDFILE* handle = sf_open(guardedFileName.getText(), SFM_READ, &info);
		
		assert(handle == 0, env);
		
		me.setLong("frames", info.frames);
		me.setInt("samplerate", info.samplerate);
		me.setInt("channels", info.channels);
		me.setInt("format", info.format);
		me.setBoolean("seekable", info.seekable);
		
		return (jlong)(uintptr_t)handle;
	}
	catch (...) {
	}
	
	return 0;
}

/*
 * Class:     org_emmef_sndfile_SoundFile
 * Method:    openWriteable
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT jlong JNICALL Java_org_emmef_sndfile_SoundFile_openWriteable
	  (JNIEnv *env, jclass myType, jstring fileName, jobject object) {
	try {
		JavaString guardedFileName(env, fileName);
		Object me(env, object);
		SF_INFO info;
		info.channels = me.getInt("channels");
		info.samplerate = me.getInt("samplerate");
		info.format = me.getInt("format");
		
		SNDFILE* handle = sf_open(guardedFileName.getText(), SFM_WRITE, &info);
		
		assert(handle == 0, env);
		
		me.setLong("frames", info.frames);
		me.setInt("samplerate", info.samplerate);
		me.setInt("channels", info.channels);
		me.setInt("format", info.format);
		me.setBoolean("seekable", info.seekable);

		return (jlong)(uintptr_t)handle;
	}
	catch(...) {
	}
	
	return 0;
}

/*
 * Class:     org_emmef_sndfile_SoundFile
 * Method:    closeHandle
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_org_emmef_sndfile_SoundFile_closeHandle
	  (JNIEnv *env, jclass, jlong handleRepresentation) {

	SNDFILE *handle = (SNDFILE *)(uintptr_t)(handleRepresentation);
	if (handle != 0) {
		int result = sf_close(handle);
		
		assert(result != 0, env);
	}
	
	return JNI_TRUE;
}

JNIEXPORT jlong JNICALL Java_org_emmef_sndfile_SoundFile_readDouble
		(JNIEnv *env, jclass, jlong handle, jdoubleArray array, jint channels, jlong frameCount) {
			
	try {
		SNDFILE *handlePtr = (SNDFILE *)(uintptr_t)handle;
		if (handle != 0) {
			fflush(stdout);
			JavaArray<jdouble, jdoubleArray> buffer(env, array);
			sf_count_t frames = getFrameCount(env, channels, buffer.getLength(), frameCount);
			sf_count_t reads = sf_readf_double(handlePtr, buffer.getData(), frames);
			fflush(stdout);
			
			assert(reads < 0, env);
			
			return reads;
		}
	}
	catch (...) {
		fprintf(stderr, "Something happended..\n");
	}
	
	return -1;
}

/*
 * Class:     org_emmef_sndfile_SoundFile
 * Method:    readFloat
 * Signature: (J[FIJ)J
 */
JNIEXPORT jlong JNICALL Java_org_emmef_sndfile_SoundFile_readFloat
		(JNIEnv *env, jclass, jlong handle, jfloatArray array, jint channels, jlong frameCount) {

	try {
		SNDFILE *handlePtr = (SNDFILE *)(uintptr_t)handle;
		if (handle != 0) {
			JavaArray<jfloat, jfloatArray> buffer(env, array);
			sf_count_t frames = getFrameCount(env, channels, buffer.getLength(), frameCount);
			sf_count_t reads = sf_readf_float(handlePtr, buffer.getData(), frames);
			
			assert(reads < 0, env);
			
			return reads;
		}
	}
	catch (...) {
	}
	
	return -1;
}

/*
 * Class:     org_emmef_sndfile_SoundFile
 * Method:    readInteger
 * Signature: (J[IIJ)J
 */
JNIEXPORT jlong JNICALL Java_org_emmef_sndfile_SoundFile_readInteger
		(JNIEnv *env, jclass, jlong handle, jintArray array, jint channels, jlong frameCount) {

	try {
		SNDFILE *handlePtr = (SNDFILE *)(uintptr_t)handle;
		if (handle != 0) {
			JavaArray<jint, jintArray> buffer(env, array);
			sf_count_t frames = getFrameCount(env, channels, buffer.getLength(), frameCount);
			sf_count_t reads = sf_readf_int(handlePtr, buffer.getData(), frames);
			
			assert(reads < 0, env);
			
			return reads;
		}
	}
	catch (...) {
	}
	
	return -1;
}

/*
 * Class:     org_emmef_sndfile_SoundFile
 * Method:    readShort
 * Signature: (J[SIJ)J
 */
JNIEXPORT jlong JNICALL Java_org_emmef_sndfile_SoundFile_readShort
		(JNIEnv *env, jclass, jlong handle, jshortArray array, jint channels, jlong frameCount) {

	try {
		SNDFILE *handlePtr = (SNDFILE *)(uintptr_t)handle;
		if (handle != 0) {
			JavaArray<jshort, jshortArray> buffer(env, array);
			sf_count_t frames = getFrameCount(env, channels, buffer.getLength(), frameCount);
			sf_count_t reads = sf_readf_short(handlePtr, buffer.getData(), frames);
			
			assert(reads < 0, env);
			
			return reads;
		}
	}
	catch (...) {
	}
	
	return -1;
}

/*
 * Class:     org_emmef_sndfile_SoundFile
 * Method:    writeDouble
 * Signature: (J[DIJ)J
 */
JNIEXPORT jlong JNICALL Java_org_emmef_sndfile_SoundFile_writeDouble
		(JNIEnv *env, jclass, jlong handle, jdoubleArray array, jint channels, jlong frameCount) {
			
	try {
		SNDFILE *handlePtr = (SNDFILE *)(uintptr_t)handle;
		if (handle != 0) {
			JavaArray<jdouble, jdoubleArray> buffer(env, array);
			sf_count_t frames = getFrameCount(env, channels, buffer.getLength(), frameCount);
			sf_count_t writes = sf_writef_double(handlePtr, buffer.getData(), frames);
			
			assert(writes < 0, env);
			
			return writes;
		}
	}
	catch (...) {
	}
	
	return -1;
}

/*
 * Class:     org_emmef_sndfile_SoundFile
 * Method:    writeFloat
 * Signature: (J[FIJ)J
 */
JNIEXPORT jlong JNICALL Java_org_emmef_sndfile_SoundFile_writeFloat
		(JNIEnv *env, jclass, jlong handle, jfloatArray array, jint channels, jlong frameCount) {
			
	try {
		SNDFILE *handlePtr = (SNDFILE *)(uintptr_t)handle;
		if (handle != 0) {
			JavaArray<jfloat, jfloatArray> buffer(env, array);
			sf_count_t frames = getFrameCount(env, channels, buffer.getLength(), frameCount);
			sf_count_t writes = sf_writef_float(handlePtr, buffer.getData(), frames);
			
			assert(writes < 0, env);
			
			return writes;
		}
	}
	catch (...) {
	}
	
	return -1;
}

/*
 * Class:     org_emmef_sndfile_SoundFile
 * Method:    writeInteger
 * Signature: (J[IIJ)J
 */
JNIEXPORT jlong JNICALL Java_org_emmef_sndfile_SoundFile_writeInteger
		(JNIEnv *env, jclass, jlong handle, jintArray array, jint channels, jlong frameCount) {
			
	try {
		SNDFILE *handlePtr = (SNDFILE *)(uintptr_t)handle;
		if (handle != 0) {
			JavaArray<jint, jintArray> buffer(env, array);
			sf_count_t frames = getFrameCount(env, channels, buffer.getLength(), frameCount);
			sf_count_t writes = sf_writef_int(handlePtr, buffer.getData(), frames);
			
			assert(writes < 0, env);
			
			return writes;
		}
	}
	catch (...) {
	}
	
	return -1;
}

/*
 * Class:     org_emmef_sndfile_SoundFile
 * Method:    writeShort
 * Signature: (J[SIJ)J
 */
JNIEXPORT jlong JNICALL Java_org_emmef_sndfile_SoundFile_writeShort
		(JNIEnv *env, jclass, jlong handle, jshortArray array, jint channels, jlong frameCount) {
			
	try {
		SNDFILE *handlePtr = (SNDFILE *)(uintptr_t)handle;
		if (handle != 0) {
			JavaArray<jshort, jshortArray> buffer(env, array);
			sf_count_t frames = getFrameCount(env, channels, buffer.getLength(), frameCount);
			sf_count_t writes = sf_writef_short(handlePtr, buffer.getData(), frames);
			
			assert(writes < 0, env);
			
			return writes;
		}
	}
	catch (...) {
	}
	
	return -1;
}

/*
 * Class:     org_emmef_sndfile_SoundFile
 * Method:    seek
 * Signature: (JJ)J
 */
JNIEXPORT jlong JNICALL Java_org_emmef_sndfile_SoundFile_seek
		(JNIEnv *env, jclass, jlong handle, jlong position, jobject whence) {
			
	try {
		SNDFILE *handlePtr = (SNDFILE *)(uintptr_t)handle;
		if (handle != 0) {
			sf_count_t tell = sf_seek(handlePtr, position, convertWhence(env, whence));
				
			assert(tell < 0, env);
			
			return tell;
		}
	}
	catch (...) {
	}
	
	return -1;
}

#ifdef __cplusplus
}
#endif

int main() {
	
	return 0;
}

