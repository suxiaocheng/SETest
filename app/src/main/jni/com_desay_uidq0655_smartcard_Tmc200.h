/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_desay_openmobile_Tmc200 */

#ifndef _Included_com_desay_openmobile_Tmc200
#define _Included_com_desay_openmobile_Tmc200
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_desay_openmobile_Tmc200
 * Method:    open
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_desay_openmobile_Tmc200_open
  (JNIEnv *, jobject);

/*
 * Class:     com_desay_openmobile_Tmc200
 * Method:    close
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_desay_openmobile_Tmc200_close
  (JNIEnv *, jobject);

/*
 * Class:     com_desay_openmobile_Tmc200
 * Method:    transmit
 * Signature: ([B)[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_desay_openmobile_Tmc200_transmit
  (JNIEnv *, jobject, jbyteArray);

/*
 * Class:     com_desay_openmobile_Tmc200
 * Method:    reset
 * Signature: ()[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_desay_openmobile_Tmc200_reset
  (JNIEnv *, jobject);

/*
 * Class:     com_desay_openmobile_Tmc200
 * Method:    getATR
 * Signature: ()[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_desay_openmobile_Tmc200_getATR
  (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif