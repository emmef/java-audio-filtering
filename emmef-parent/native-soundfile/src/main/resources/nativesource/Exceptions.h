#ifndef EMMEF_SNDFILE_NATIVE_EXCEPTIONS
#define EMMEF_SNDFILE_NATIVE_EXCEPTIONS

#include <string>
#include <jni.h>

bool initExceptions(JNIEnv *env);
jthrowable getThrowable(JNIEnv* env, const char *typeName, const char* message);
	
class JavaException {
	JNIEnv* const _env;
	const jthrowable _throwable; 
	
public : 
	JavaException(JNIEnv* env, const char* type, const char *message) : _env(env), _throwable(getThrowable(env, type, message)) {
	}

	~JavaException() {
		_env->Throw(_throwable);
	}
};

class IndexOutOfBoundsException : public JavaException {
	public : 
	const static char* className;
	IndexOutOfBoundsException(JNIEnv* env, const char* message) : JavaException(env, className, message) {
	}
};

class IllegalStateException : public JavaException {
	public : 
	const static char* className;
	IllegalStateException(JNIEnv* env, const char* message) : JavaException(env, className, message) {
	}
};

class IllegalArgumentException : public JavaException {
	public : 
	const static char* className;
	IllegalArgumentException(JNIEnv* env, const char* message) : JavaException(env, className, message) {
	}
};


class IOException : public JavaException {
	public : 
	const static char* className;
	IOException(JNIEnv* env, const char* message) : JavaException(env, className, message) {
	}
};


#endif // EMMEF_SNDFILE_NATIVE_EXCEPTIONS
