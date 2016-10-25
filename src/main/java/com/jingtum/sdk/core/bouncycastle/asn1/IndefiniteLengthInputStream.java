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

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

class IndefiniteLengthInputStream
    extends LimitedInputStream
{
    private int _b1;
    private int _b2;
    private boolean _eofReached = false;
    private boolean _eofOn00 = true;

    IndefiniteLengthInputStream(
        InputStream in,
        int         limit)
        throws IOException
    {
        super(in, limit);

        _b1 = in.read();
        _b2 = in.read();

        if (_b2 < 0)
        {
            // Corrupted stream
            throw new EOFException();
        }

        checkForEof();
    }

    void setEofOn00(
        boolean eofOn00)
    {
        _eofOn00 = eofOn00;
        checkForEof();
    }

    private boolean checkForEof()
    {
        if (!_eofReached && _eofOn00 && (_b1 == 0x00 && _b2 == 0x00))
        {
            _eofReached = true;
            setParentEofDetect(true);
        }
        return _eofReached;
    }

    public int read(byte[] b, int off, int len)
        throws IOException
    {
        // Only use this optimisation if we aren't checking for 00
        if (_eofOn00 || len < 3)
        {
            return super.read(b, off, len);
        }

        if (_eofReached)
        {
            return -1;
        }

        int numRead = _in.read(b, off + 2, len - 2);

        if (numRead < 0)
        {
            // Corrupted stream
            throw new EOFException();
        }

        b[off] = (byte)_b1;
        b[off + 1] = (byte)_b2;

        _b1 = _in.read();
        _b2 = _in.read();

        if (_b2 < 0)
        {
            // Corrupted stream
            throw new EOFException();
        }

        return numRead + 2;
    }

    public int read()
        throws IOException
    {
        if (checkForEof())
        {
            return -1;
        }

        int b = _in.read();

        if (b < 0)
        {
            // Corrupted stream
            throw new EOFException();
        }

        int v = _b1;

        _b1 = _b2;
        _b2 = b;

        return v;
    }
}
