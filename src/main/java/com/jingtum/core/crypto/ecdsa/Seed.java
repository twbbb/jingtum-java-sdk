/*
 * Copyright www.jingtum.com Inc. 
 * 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.jingtum.core.crypto.ecdsa;

import com.jingtum.core.utils.Utils;
import com.jingtum.exception.InvalidParameterException;
import com.jingtum.Jingtum;
import com.jingtum.JingtumMessage;
import com.jingtum.core.utils.HashUtils;
import com.jingtum.core.utils.Sha512;

import static com.jingtum.core.config.Config.getB58IdentiferCodecs;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

public class Seed {
	final byte[] seedBytes;
    /**
     *
     * @param secretKey secret point on the curve as BigInteger
     * @return corresponding public point
     */
    private static byte[] getPublic(BigInteger secretKey) {
        return SECP256K1.basePointMultipliedBy(secretKey);
    }
    
    public Seed(byte[] seedBytes) {
        this.seedBytes = seedBytes;
    }
    
    @Override
    public String toString() {
        return getB58IdentiferCodecs().encodeFamilySeed(seedBytes);
    }

    /**
     *
     * @param privateGen secret point on the curve as BigInteger
     * @return the corresponding public key is the public generator
     *         (aka public root key, master public key).
     *         return as byte[] for convenience.
     */
    private static byte[] computePublicGenerator(BigInteger privateGen) {
        return getPublic(privateGen);
    }

	private static BigInteger computePublicKey(BigInteger secret) {
        return Utils.uBigInt(getPublic(secret));
    }

    private static BigInteger computePrivateGen(byte[] seedBytes) {
        byte[] privateGenBytes;
        BigInteger privateGen;
        int i = 0;

        while (true) {
            privateGenBytes = new Sha512().add(seedBytes)
                                          .add32(i++)
                                          .finish256();
            privateGen = Utils.uBigInt(privateGenBytes);
            if (privateGen.compareTo(SECP256K1.order()) == -1) {
                break;
            }
        }
        return privateGen;
    }

    private static BigInteger computeSecretKey(BigInteger privateGen, byte[] publicGenBytes, int accountNumber) {
        BigInteger secret;
        int i;

        i=0;
        while (true) {
            byte[] secretBytes = new Sha512().add(publicGenBytes)
                                             .add32(accountNumber)
                                             .add32(i++)
                                             .finish256();
            secret = Utils.uBigInt(secretBytes);
            if (secret.compareTo(SECP256K1.order()) == -1) {
                break;
            }
        }

        secret = secret.add(privateGen).mod(SECP256K1.order());
        return secret;
    }
    
    public static String computeAddress(String secret) throws InvalidParameterException{
    	byte[] pubBytes = null; 
    	IKeyPair keyPair = Seed.getKeyPair(secret);    	
        pubBytes = HashUtils.SHA256_RIPEMD160(keyPair.pub().toByteArray());        
        return getB58IdentiferCodecs().encodeAddress(pubBytes);
    }
    
    public static IKeyPair getKeyPair(byte[] seedBytes) {
        return createKeyPair(seedBytes, 0);
    }

    public static IKeyPair getKeyPair(String b58) throws InvalidParameterException {
    	byte[] bytes = null;
    	try{
    		bytes = getB58IdentiferCodecs().decodeFamilySeed(b58);
    	}catch(Exception e){
    		throw new InvalidParameterException(JingtumMessage.INVALID_SECRET,b58,null);
    	}
        return getKeyPair(bytes);
    }
    
    public static IKeyPair createKeyPair(byte[] seedBytes, int accountNumber) {
        BigInteger secret, pub, privateGen;
        // The private generator (aka root private key, master private key)
        privateGen = computePrivateGen(seedBytes);
        byte[] publicGenBytes = computePublicGenerator(privateGen);

        if (accountNumber == -1) {
            // The root keyPair
            return new KeyPair(privateGen, Utils.uBigInt(publicGenBytes));
        }
        else {
            secret = computeSecretKey(privateGen, publicGenBytes, accountNumber);
            pub = computePublicKey(secret);
            return new KeyPair(secret, pub);
        }
    }
    
    public static Seed fromPassPhrase(String passPhrase) {
        return new Seed(passPhraseToSeedBytes(passPhrase));
    }

    public static byte[] passPhraseToSeedBytes(String phrase) {
        try {
            return new Sha512(phrase.getBytes("utf-8")).finish128();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static String generateSecret(){
		long timestamp = System.currentTimeMillis();
		String seedString = Jingtum.getSignString() + timestamp; 
		return fromPassPhrase(seedString).toString();
    }
}


