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
import java.io.InputStream;
import java.io.OutputStream;

public class BERGenerator
    extends ASN1Generator
{
    private boolean      _tagged = false;
    private boolean      _isExplicit;
    private int          _tagNo;
    
    protected BERGenerator(
        OutputStream out)
    {
        super(out);
    }

    public BERGenerator(
        OutputStream out,
        int tagNo,
        boolean isExplicit) 
    {
        super(out);
        
        _tagged = true;
        _isExplicit = isExplicit;
        _tagNo = tagNo;
    }

    public OutputStream getRawOutputStream()
    {
        return _out;
    }
    
    private void writeHdr(
        int tag)
        throws IOException
    {
        _out.write(tag);
        _out.write(0x80);
    }
    
    protected void writeBERHeader(
        int tag) 
        throws IOException
    {
        if (_tagged)
        {
            int tagNum = _tagNo | BERTags.TAGGED;

            if (_isExplicit)
            {
                writeHdr(tagNum | BERTags.CONSTRUCTED);
                writeHdr(tag);
            }
            else
            {   
                if ((tag & BERTags.CONSTRUCTED) != 0)
                {
                    writeHdr(tagNum | BERTags.CONSTRUCTED);
                }
                else
                {
                    writeHdr(tagNum);
                }
            }
        }
        else
        {
            writeHdr(tag);
        }
    }
    
    protected void writeBERBody(
        InputStream contentStream)
        throws IOException
    {
        int ch;
        
        while ((ch = contentStream.read()) >= 0)
        {
            _out.write(ch);
        }
    }

    protected void writeBEREnd()
        throws IOException
    {
        _out.write(0x00);
        _out.write(0x00);
        
        if (_tagged && _isExplicit)  // write extra end for tag header
        {
            _out.write(0x00);
            _out.write(0x00);
        }
    }
}
