/**
 * Copyright@2016 Jingtum Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jingtum.sdk.core.bouncycastle.asn1;

import java.math.BigInteger;

public class ASN1Enumerated
    extends DEREnumerated
{
    ASN1Enumerated(byte[] bytes)
    {
        super(bytes);
    }

    public ASN1Enumerated(BigInteger value)
    {
        super(value);
    }

    public ASN1Enumerated(int value)
    {
        super(value);
    }
}