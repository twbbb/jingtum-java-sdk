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

import com.jingtum.sdk.core.bouncycastle.asn1.x9.X9IntegerConverter;

/**
 * base class for points on elliptic curves.
 */
public abstract class ECPoint
{
    ECCurve        curve;
    ECFieldElement x;
    ECFieldElement y;

    protected boolean withCompression;

    protected ECMultiplier multiplier = null;

    protected PreCompInfo preCompInfo = null;

    private static X9IntegerConverter converter = new X9IntegerConverter();

    protected ECPoint(ECCurve curve, ECFieldElement x, ECFieldElement y)
    {
        this.curve = curve;
        this.x = x;
        this.y = y;
    }
    
    public ECFieldElement getX()
    {
        return x;
    }

    public ECFieldElement getY()
    {
        return y;
    } 

    public boolean isInfinity()
    {
        return x == null && y == null;
    }

    public abstract byte[] getEncoded(boolean compressed);

    public abstract ECPoint add(ECPoint b);
    public abstract ECPoint negate();
    public abstract ECPoint twice();

    /**
     * Sets the default <code>ECMultiplier</code>, unless already set. 
     */
    synchronized void assertECMultiplier()
    {
        if (this.multiplier == null)
        {
            this.multiplier = new FpNafMultiplier();
        }
    }

    /**
     * Multiplies this <code>ECPoint</code> by the given number.
     * @param k The multiplicator.
     * @return <code>k * this</code>.
     */
    public ECPoint multiply(BigInteger k)
    {
        if (k.signum() < 0)
        {
            throw new IllegalArgumentException("The multiplicator cannot be negative");
        }

        if (this.isInfinity())
        {
            return this;
        }

        if (k.signum() == 0)
        {
            return this.curve.getInfinity();
        }

        assertECMultiplier();
        return this.multiplier.multiply(this, k, preCompInfo);
    }

    /**
     * Elliptic curve points over Fp
     */
    public static class Fp extends ECPoint
    {
        
        /**
         * Create a point which encodes with point compression.
         * 
         * @param curve the curve to use
         * @param x affine x co-ordinate
         * @param y affine y co-ordinate
         */
        public Fp(ECCurve curve, ECFieldElement x, ECFieldElement y)
        {
            this(curve, x, y, false);
        }

        /**
         * Create a point that encodes with or without point compresion.
         * 
         * @param curve the curve to use
         * @param x affine x co-ordinate
         * @param y affine y co-ordinate
         * @param withCompression if true encode with point compression
         */
        public Fp(ECCurve curve, ECFieldElement x, ECFieldElement y, boolean withCompression)
        {
            super(curve, x, y);

            if ((x != null && y == null) || (x == null && y != null))
            {
                throw new IllegalArgumentException("Exactly one of the field elements is null");
            }

            this.withCompression = withCompression;
        }
         
        /**
         * return the field element encoded with point compression. (S 4.3.6)
         */
        public byte[] getEncoded(boolean compressed)
        {
            if (this.isInfinity()) 
            {
                return new byte[1];
            }

            int qLength = converter.getByteLength(x);
            
            if (compressed)
            {
                byte    PC;
    
                if (this.getY().toBigInteger().testBit(0))
                {
                    PC = 0x03;
                }
                else
                {
                    PC = 0x02;
                }
    
                byte[]  X = converter.integerToBytes(this.getX().toBigInteger(), qLength);
                byte[]  PO = new byte[X.length + 1];
    
                PO[0] = PC;
                System.arraycopy(X, 0, PO, 1, X.length);
    
                return PO;
            }
            else
            {
                byte[]  X = converter.integerToBytes(this.getX().toBigInteger(), qLength);
                byte[]  Y = converter.integerToBytes(this.getY().toBigInteger(), qLength);
                byte[]  PO = new byte[X.length + Y.length + 1];
                
                PO[0] = 0x04;
                System.arraycopy(X, 0, PO, 1, X.length);
                System.arraycopy(Y, 0, PO, X.length + 1, Y.length);

                return PO;
            }
        }

        // B.3 pg 62
        public ECPoint add(ECPoint b)
        {
            if (this.isInfinity())
            {
                return b;
            }

            if (b.isInfinity())
            {
                return this;
            }

            // Check if b = this or b = -this
            if (this.x.equals(b.x))
            {
                if (this.y.equals(b.y))
                {
                    // this = b, i.e. this must be doubled
                    return this.twice();
                }

                // this = -b, i.e. the result is the point at infinity
                return this.curve.getInfinity();
            }

            ECFieldElement gamma = b.y.subtract(this.y).divide(b.x.subtract(this.x));

            ECFieldElement x3 = gamma.square().subtract(this.x).subtract(b.x);
            ECFieldElement y3 = gamma.multiply(this.x.subtract(x3)).subtract(this.y);

            return new ECPoint.Fp(curve, x3, y3, withCompression);
        }

        // B.3 pg 62
        public ECPoint twice()
        {
            if (this.isInfinity())
            {
                // Twice identity element (point at infinity) is identity
                return this;
            }

            if (this.y.toBigInteger().signum() == 0) 
            {
                // if y1 == 0, then (x1, y1) == (x1, -y1)
                // and hence this = -this and thus 2(x1, y1) == infinity
                return this.curve.getInfinity();
            }

            ECFieldElement TWO = this.curve.fromBigInteger(BigInteger.valueOf(2));
            ECFieldElement THREE = this.curve.fromBigInteger(BigInteger.valueOf(3));
            ECFieldElement gamma = this.x.square().multiply(THREE).add(curve.a).divide(y.multiply(TWO));

            ECFieldElement x3 = gamma.square().subtract(this.x.multiply(TWO));
            ECFieldElement y3 = gamma.multiply(this.x.subtract(x3)).subtract(this.y);
                
            return new ECPoint.Fp(curve, x3, y3, this.withCompression);
        }

        public ECPoint negate()
        {
            return new ECPoint.Fp(curve, this.x, this.y.negate(), this.withCompression);
        }

    }
}
