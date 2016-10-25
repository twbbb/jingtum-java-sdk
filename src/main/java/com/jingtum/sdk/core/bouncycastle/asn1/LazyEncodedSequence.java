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

package com.jingtum.sdk.core.bouncycastle.asn1;

import java.io.IOException;
import java.util.Enumeration;

/**
 * Note: this class is for processing DER/DL encoded sequences only.
 */
class LazyEncodedSequence
    extends ASN1Sequence
{
    private byte[] encoded;

    LazyEncodedSequence(
        byte[] encoded)
        throws IOException
    {
        this.encoded = encoded;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	private void parse()
    {
        Enumeration en = new LazyConstructionEnumeration(encoded);

        while (en.hasMoreElements())
        {
            seq.addElement(en.nextElement());
        }

        encoded = null;
    }

    public synchronized ASN1Encodable getObjectAt(int index)
    {
        if (encoded != null)
        {
            parse();
        }

        return super.getObjectAt(index);
    }

    @SuppressWarnings("rawtypes")
	public synchronized Enumeration getObjects()
    {
        if (encoded == null)
        {
            return super.getObjects();
        }

        return new LazyConstructionEnumeration(encoded);
    }

    public synchronized int size()
    {
        if (encoded != null)
        {
            parse();
        }

        return super.size();
    }

    ASN1Primitive toDERObject()
    {
        if (encoded != null)
        {
            parse();
        }

        return super.toDERObject();
    }

    ASN1Primitive toDLObject()
    {
        if (encoded != null)
        {
            parse();
        }

        return super.toDLObject();
    }

    int encodedLength()
        throws IOException
    {
        if (encoded != null)
        {
            return 1 + StreamUtil.calculateBodyLength(encoded.length) + encoded.length;
        }
        else
        {
            return super.toDLObject().encodedLength();
        }
    }

    void encode(
        ASN1OutputStream out)
        throws IOException
    {
        if (encoded != null)
        {
            out.writeEncoded(BERTags.SEQUENCE | BERTags.CONSTRUCTED, encoded);
        }
        else
        {
            super.toDLObject().encode(out);
        }
    }
}
