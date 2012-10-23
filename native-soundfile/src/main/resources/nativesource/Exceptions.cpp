#include "Exceptions.h"

const char *IndexOutOfBoundsException::className = "java/lang/IndexOutOfBoundsException";
const char *IllegalStateException::className = "java/lang/IllegalStateException";
const char *IllegalArgumentException::className = "java/lang/IllegalArgumentException";
const char *IOException::className = "java/io/IOException";

jthrowable getThrowable(JNIEnv* env, const char *typeName, const char* message) {
	const char *error = 0;
	if (typeName == 0) {
		error = "Type name for exception is null";
	}
	else {
		const jclass exception = env->FindClass("java/lang/Exception");
		if (exception == 0) {
			error = "Class java/lang/Exception not found!";
		}
		else {
			jclass type = env->FindClass(typeName);
			if (type == 0) {
				error = "Exception classtype was not found";
			}
			else if (!env->IsAssignableFrom(type, exception)) {
				error = "Exception classtype is not derived from java/lang/Exception";
			}
			else if (message == 0) {
				error = "No message";
			}
			else {
				jmethodID constructor = env->GetMethodID(type, "<init>", "(Ljava/lang/String;)V");
				if (constructor == 0) {
					error = "Exception classtype has no constructor with one string as an argument";
				}
				else {
					jthrowable result = (jthrowable)env->NewObject(type, constructor, env->NewStringUTF(message));
					if (result == 0) {
						error = "Instantiation of exception failed";
					}
					else {
						return result;
					}
				}
			}
		}
	}
	fprintf(stderr, "%s: %s\n", error, typeName);
	fflush(stderr);
	env->ThrowNew(env->FindClass("java/lang/Error"), error);
	
	return 0;
}
	
static bool checkClass(JNIEnv *env, const char* className) {
	return env->FindClass(className) != 0;
}

bool initExceptions(JNIEnv *env) {
	static bool init = false;
	
	init |= 
		checkClass(env, IndexOutOfBoundsException::className) &&
		checkClass(env, IllegalStateException::className) &&
		checkClass(env, IllegalArgumentException::className) &&
		checkClass(env, IOException::className);
	
	return init;
}
