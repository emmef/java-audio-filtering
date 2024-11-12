#ifndef SOUNDFILE_CLASS 
#define SOUNDFILE_CLASS 

#include <jni.h>
#include "Exceptions.h"

typedef std::string String;

class Object {
	public:
		Object(JNIEnv *env, jobject &object) : _env(env), _class(env->GetObjectClass(object)), _object(object) {
			if (_class == 0) {
				throw IllegalArgumentException(env, "Parameter 'env' cannot be null");
			}
			else if (_object == 0) {
				throw IllegalArgumentException(env, "Parameter 'object' cannot be null");
			}
		}

		void setString(const char* fieldName, const char* value) {
			_env->SetObjectField(_object, getFieldID(fieldName, "Ljava/lang/String;"), _env->NewStringUTF(value));
		}	
		
		void setString(const char* fieldName, String &value) {
			_env->SetObjectField(_object, getFieldID(fieldName, "Ljava/lang/String;"), _env->NewStringUTF(value.c_str()));
		}			
		
		void getString(const char* fieldName, String &result) {
			jobject str = _env->GetObjectField(_object, getFieldID(fieldName, "Ljava/lang/String;"));
			if (str != 0) {
				jboolean isCopy;
				const char* chars = _env->GetStringUTFChars((jstring)str, &isCopy);
				result = chars;
				_env->ReleaseStringUTFChars((jstring)str, chars);
			}
			else {
				result = "";
			}
		}	
		
		void setBoolean(const char* fieldName, bool newValue) {
			_env->SetBooleanField(_object, getFieldID(fieldName, "Z"), (jboolean)newValue);
		}
		
		bool getBoolean(const char* fieldName) {
			return _env->GetBooleanField(_object, getFieldID(fieldName, "Z")) == JNI_TRUE;
		}
		
		void setDouble(const char* fieldName, double newValue) {
			_env->SetDoubleField(_object, getFieldID(fieldName, "D"), (jdouble)newValue);
		}
		
		double getDouble(const char* fieldName) {
			return (double)_env->GetDoubleField(_object, getFieldID(fieldName, "D"));
		}
		
		void setLong(const char* fieldName, long long newValue) {
			_env->SetLongField(_object, getFieldID(fieldName, "J"), (jlong)newValue);
		}
		
		long getLong(const char* fieldName) {
			return _env->GetLongField(_object, getFieldID(fieldName, "J"));
		}
		
		void setInt(const char* fieldName, int newValue) {
			_env->SetIntField(_object, getFieldID(fieldName, "I"), (jint)newValue);
		}
		
		int getInt(const char* fieldName) {
			return _env->GetIntField(_object, getFieldID(fieldName, "I"));
		}
		
	private :
		JNIEnv *_env;
		const jclass _class;
		const jobject &_object;
		
		jfieldID getFieldID(const char* name, const char* signature) {
			jfieldID id = _env->GetFieldID(_class, name, signature);
			if (id == 0) {
				fprintf(stderr, "Class has no field \"%s\" with signature %s\n", name, signature);
				throw IllegalArgumentException(_env, "Illegal field or wrong type specified");
			}
			return id;			
		}
};

class JavaString {
	JNIEnv *_env;
	const jstring _str;
	jboolean isCopy;
	const char* characters;
	
	public : 
	
		JavaString(JNIEnv *env, jstring str) : _env(env), _str(str), characters(_env->GetStringUTFChars(_str, &isCopy)) {
		}
		
		const char *const getText() const {
			return characters;
		}
		
		~JavaString() {
			_env->ReleaseStringUTFChars(_str, characters);
		}
};

#endif // SOUNDFILE_CLASS 
