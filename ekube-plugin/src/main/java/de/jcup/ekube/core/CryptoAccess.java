/*
 * Copyright 2019 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
 package de.jcup.ekube.core;

import java.io.Serializable;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;

public class CryptoAccess<T extends Serializable> {
	private static KeyGenerator keyGen;
	private char[] transformation = new char[] {'A','E','S'};
	private SecretKey secretKey;

	public static final CryptoAccess<String> CRYPTO_STRING = new CryptoAccess<String>();

	public CryptoAccess(){
		secretKey = getkeyGen(transformation).generateKey();
	}

	private static KeyGenerator getkeyGen(char[] transformation) {
		if (CryptoAccess.keyGen!=null) {
			return CryptoAccess.keyGen;
		}
		try {
			KeyGenerator keyGen = KeyGenerator.getInstance(new String(transformation));
			keyGen.init(128);
			CryptoAccess.keyGen=keyGen;
			return CryptoAccess.keyGen;

		} catch (Exception e) {
			throw new IllegalStateException("FATAL:cannot create key generator",e);
		}
	}
	public SealedObject seal(T object) {
		try {
			Cipher cipher = Cipher.getInstance(new String(transformation));
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, new SecureRandom());
			return new SealedObject(object,cipher);
		} catch (Exception e) {
			throw new IllegalStateException("cannot create sealed object for given objects",e);
		}
	}

	@SuppressWarnings("unchecked")
	public T unseal(SealedObject object) {
		try {
			if (object==null) {
				return null;
			}
			Cipher cipher = Cipher.getInstance(new String(transformation));
			cipher.init(Cipher.DECRYPT_MODE, secretKey, new SecureRandom());
			return (T) object.getObject(cipher);
		} catch (Exception e) {
			throw new IllegalStateException("cannot create sealed object for given objects",e);
		}
	}
}
