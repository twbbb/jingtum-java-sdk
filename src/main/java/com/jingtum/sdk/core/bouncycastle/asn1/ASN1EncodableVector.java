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

import java.util.Enumeration;
import java.util.Vector;

public class ASN1EncodableVector
{
    @SuppressWarnings("rawtypes")
	Vector v = new Vector();

    public ASN1EncodableVector()
    {
    }

    @SuppressWarnings("unchecked")
	public void add(ASN1Encodable obj)
    {
        v.addElement(obj);
    }

    @SuppressWarnings("unchecked")
	public void addAll(ASN1EncodableVector other)
    {
        for (@SuppressWarnings("rawtypes")
		Enumeration en = other.v.elements(); en.hasMoreElements();)
        {
            v.addElement(en.nextElement());
        }
    }

    public ASN1Encodable get(int i)
    {
        return (ASN1Encodable)v.elementAt(i);
    }

    public int size()
    {
        return v.size();
    }
}
