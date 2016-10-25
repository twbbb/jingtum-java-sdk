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

package com.jingtum.sdk.core.bouncycastle.math.ec;

import java.math.BigInteger;

/**
 * base class for an elliptic curve
 */
public abstract class ECCurve
{
    ECFieldElement a, b;

    public abstract int getFieldSize();

    public abstract ECFieldElement fromBigInteger(BigInteger x);

    public abstract ECPoint createPoint(BigInteger x, BigInteger y, boolean withCompression);

    public abstract ECPoint getInfinity();

    protected abstract ECPoint decompressPoint(int yTilde, BigInteger X1);

    /**
     * Decode a point on this curve from its ASN.1 encoding. The different
     * encodings are taken account of, including point compression for
     * <code>F<sub>p</sub></code> (X9.62 s 4.2.1 pg 17).
     * @return The decoded point.
     */
    public ECPoint decodePoint(byte[] encoded)
    {
        ECPoint p = null;
        int expectedLength = (getFieldSize() + 7) / 8;

        switch (encoded[0])
        {
        case 0x00: // infinity
        {
            if (encoded.length != 1)
            {
                throw new IllegalArgumentException("Incorrect length for infinity encoding");
            }

            p = getInfinity();
            break;
        }
        case 0x02: // compressed
        case 0x03: // compressed
        {
            if (encoded.length != (expectedLength + 1))
            {
                throw new IllegalArgumentException("Incorrect length for compressed encoding");
            }

            int yTilde = encoded[0] & 1;
            BigInteger X1 = fromArray(encoded, 1, expectedLength);

            p = decompressPoint(yTilde, X1);
            break;
        }
        case 0x04: // uncompressed
        case 0x06: // hybrid
        case 0x07: // hybrid
        {
            if (encoded.length != (2 * expectedLength + 1))
            {
                throw new IllegalArgumentException("Incorrect length for uncompressed/hybrid encoding");
            }

            BigInteger X1 = fromArray(encoded, 1, expectedLength);
            BigInteger Y1 = fromArray(encoded, 1 + expectedLength, expectedLength);

            p = createPoint(X1, Y1, false);
            break;
        }
        default:
            throw new IllegalArgumentException("Invalid point encoding 0x" + Integer.toString(encoded[0], 16));
        }

        return p;
    }

    private static BigInteger fromArray(byte[] buf, int off, int length)
    {
        byte[] mag = new byte[length];
        System.arraycopy(buf, off, mag, 0, length);
        return new BigInteger(1, mag);
    }

    /**
     * Elliptic curve over Fp
     */
    public static class Fp extends ECCurve
    {
        BigInteger q;
        ECPoint.Fp infinity;

        public Fp(BigInteger q, BigInteger a, BigInteger b)
        {
            this.q = q;
            this.a = fromBigInteger(a);
            this.b = fromBigInteger(b);
            this.infinity = new ECPoint.Fp(this, null, null);
        }

        public int getFieldSize()
        {
            return q.bitLength();
        }

        public ECFieldElement fromBigInteger(BigInteger x)
        {
            return new ECFieldElement.Fp(this.q, x);
        }

        public ECPoint createPoint(BigInteger x, BigInteger y, boolean withCompression)
        {
            return new ECPoint.Fp(this, fromBigInteger(x), fromBigInteger(y), withCompression);
        }

        protected ECPoint decompressPoint(int yTilde, BigInteger X1)
        {
            ECFieldElement x = fromBigInteger(X1);
            ECFieldElement alpha = x.multiply(x.square().add(a)).add(b);
            ECFieldElement beta = alpha.sqrt();

            //
            // if we can't find a sqrt we haven't got a point on the
            // curve - run!
            //
            if (beta == null)
            {
                throw new RuntimeException("Invalid point compression");
            }

            BigInteger betaValue = beta.toBigInteger();
            int bit0 = betaValue.testBit(0) ? 1 : 0;

            if (bit0 != yTilde)
            {
                // Use the other root
                beta = fromBigInteger(q.subtract(betaValue));
            }

            return new ECPoint.Fp(this, x, beta, true);
        }

        public ECPoint getInfinity()
        {
            return infinity;
        }
    }
}
