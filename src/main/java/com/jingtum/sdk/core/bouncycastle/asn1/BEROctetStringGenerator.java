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
import java.io.OutputStream;

public class BEROctetStringGenerator
    extends BERGenerator
{
    public BEROctetStringGenerator(OutputStream out) 
        throws IOException
    {
        super(out);
        
        writeBERHeader(BERTags.CONSTRUCTED | BERTags.OCTET_STRING);
    }

    public BEROctetStringGenerator(
        OutputStream out,
        int tagNo,
        boolean isExplicit) 
        throws IOException
    {
        super(out, tagNo, isExplicit);
        
        writeBERHeader(BERTags.CONSTRUCTED | BERTags.OCTET_STRING);
    }
    
    public OutputStream getOctetOutputStream()
    {
        return getOctetOutputStream(new byte[1000]); // limit for CER encoding.
    }

    public OutputStream getOctetOutputStream(
        byte[] buf)
    {
        return new BufferedBEROctetStream(buf);
    }
   
    private class BufferedBEROctetStream
        extends OutputStream
    {
        private byte[] _buf;
        private int    _off;
        private DEROutputStream _derOut;

        BufferedBEROctetStream(
            byte[] buf)
        {
            _buf = buf;
            _off = 0;
            _derOut = new DEROutputStream(_out);
        }
        
        public void write(
            int b)
            throws IOException
        {
            _buf[_off++] = (byte)b;

            if (_off == _buf.length)
            {
                DEROctetString.encode(_derOut, _buf);
                _off = 0;
            }
        }

        public void write(byte[] b, int off, int len) throws IOException
        {
            while (len > 0)
            {
                int numToCopy = Math.min(len, _buf.length - _off);
                System.arraycopy(b, off, _buf, _off, numToCopy);

                _off += numToCopy;
                if (_off < _buf.length)
                {
                    break;
                }

                DEROctetString.encode(_derOut, _buf);
                _off = 0;

                off += numToCopy;
                len -= numToCopy;
            }
        }

        public void close() 
            throws IOException
        {
            if (_off != 0)
            {
                byte[] bytes = new byte[_off];
                System.arraycopy(_buf, 0, bytes, 0, _off);
                
                DEROctetString.encode(_derOut, bytes);
            }
            
             writeBEREnd();
        }
    }
}
