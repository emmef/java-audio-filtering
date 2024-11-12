#ifndef EMMEF_SNDFILE_NATIVE_JAVAARRAYS
#define EMMEF_SNDFILE_NATIVE_JAVAARRAYS

#include "Exceptions.h"

class ArrayHandle {
public : 
	enum ArrayType { BOOLEAN, BYTE, CHAR, SHORT, INTEGER, LONG, FLOAT, DOUBLE };
	
	ArrayHandle(JNIEnv* environment, jbooleanArray javaArray) : env(environment), type(BOOLEAN), array(javaArray), length(env->GetArrayLength(array)), data(fetchData(env, type, array, length)) {
	}
	ArrayHandle(JNIEnv* environment, jbyteArray javaArray) : env(environment), type(BYTE), array(javaArray), length(env->GetArrayLength(array)), data(fetchData(env, type, array, length)) {
	}
	ArrayHandle(JNIEnv* environment, jcharArray javaArray) : env(environment), type(CHAR), array(javaArray), length(env->GetArrayLength(array)), data(fetchData(env, type, array, length)) {
	}
	ArrayHandle(JNIEnv* environment, jshortArray javaArray) : env(environment), type(SHORT), array(javaArray), length(env->GetArrayLength(array)), data(fetchData(env, type, array, length)) {
	}
	ArrayHandle(JNIEnv* environment, jintArray javaArray) : env(environment), type(INTEGER), array(javaArray), length(env->GetArrayLength(array)), data(fetchData(env, type, array, length)) {
	}
	ArrayHandle(JNIEnv* environment, jlongArray javaArray) : env(environment), type(LONG), array(javaArray), length(env->GetArrayLength(array)), data(fetchData(env, type, array, length)) {
	}
	ArrayHandle(JNIEnv* environment, jfloatArray javaArray) : env(environment), type(FLOAT), array(javaArray), length(env->GetArrayLength(array)), data(fetchData(env, type, array, length)) {
	}
	ArrayHandle(JNIEnv* environment, jdoubleArray javaArray) : env(environment), type(DOUBLE), array(javaArray), length(env->GetArrayLength(array)), data(ArrayHandle::fetchData(env, type, array, length)) {
	}
	
	virtual ~ArrayHandle() {
		if (data != 0) {
			switch(type) {
				case BOOLEAN : 
					env->ReleaseBooleanArrayElements((jbooleanArray)array, (jboolean*)data, 0);
					break;
				case BYTE : 
					env->ReleaseByteArrayElements((jbyteArray)array, (jbyte*)data, 0);
					break;
				case CHAR : 
					env->ReleaseCharArrayElements((jcharArray)array, (jchar*)data, 0);
					break;
				case SHORT : 
					env->ReleaseShortArrayElements((jshortArray)array, (jshort*)data, 0);
					break;
				case INTEGER : 
					env->ReleaseIntArrayElements((jintArray)array, (jint*)data, 0);
					break;
				case LONG : 
					env->ReleaseLongArrayElements((jlongArray)array, (jlong*)data, 0);
					break;
				case FLOAT : 
					env->ReleaseFloatArrayElements((jfloatArray)array, (jfloat*)data, 0);
					break;
				case DOUBLE : 
					env->ReleaseDoubleArrayElements((jdoubleArray)array, (jdouble*)data, 0);
					break;
			}
		}
	}
	
	ArrayType getType() {
		return type;
	}
	
	const char *const getTypeName() {
			switch(type) {
				case BOOLEAN : 
					return "boolean";
				case BYTE : 
					return " byte";
				case CHAR : 
					return "char";
				case SHORT : 
					return "short";
				case INTEGER : 
					return "integer";
				case LONG : 
					return "long";
				case FLOAT : 
					return "float";
				case DOUBLE : 
					return "double";
				default : 
					throw IllegalStateException(env, "JavaArray::getTypeName(): Unrecognized type");
			}
	}

private:
	JNIEnv * const env;
	const ArrayType type;
	const jarray array;
	
	static void* const fetchData(JNIEnv* env, ArrayType type, jarray array, jsize length) {
		if (length > 0) {
			switch(type) {
				case BOOLEAN : 
					return env->GetBooleanArrayElements((jbooleanArray)array, 0);
				case BYTE : 
					return env->GetByteArrayElements((jbyteArray)array, 0);
				case CHAR : 
					return env->GetCharArrayElements((jcharArray)array, 0);
				case SHORT : 
					return env->GetShortArrayElements((jshortArray)array, 0);
				case INTEGER : 
					return env->GetIntArrayElements((jintArray)array, 0);
				case LONG : 
					return env->GetLongArrayElements((jlongArray)array, 0);
				case FLOAT : 
					return env->GetFloatArrayElements((jfloatArray)array, 0);
				case DOUBLE : 
					return env->GetDoubleArrayElements((jdoubleArray)array, 0);
				default: 
					throw IllegalStateException(env, "JavaArray::fetchData(): Illegal array type");
			}
		}
		return 0;
	}
	
protected: 
	const jsize length;	
	void* const data;
	
	JNIEnv* const getEnvironment() const {
		return env;
	}
};

template <typename E, typename A> class JavaArray : ArrayHandle {
	public : 
		JavaArray(JNIEnv* env, A javaArray) : ArrayHandle(env, javaArray) {
//			printf("JavaArray<%s>[%i] @ %p.\n", getTypeName(), getLength(), getData());
		};
		
		inline const jsize getLength() const {
			return length;
		}
		
		inline E* const getData() const {
			return (E*)data;
		}
		
		inline E& operator[](jsize index) const {
			if (index < length) {
				return ((E*)data)[index];
			}
			else {
				JNIEnv* const env = getEnvironment();
				throw IndexOutOfBoundsException(env, "SoundFile::JavaArray::operator[]");
			}
		}
		
		inline ArrayType getType() {
			return type;
		}
};

#endif // EMMEF_SNDFILE_NATIVE_JAVAARRAYS
