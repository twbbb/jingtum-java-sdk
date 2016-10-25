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
 * Interface for classes encapsulating a point multiplication algorithm
 * for <code>ECPoint</code>s.
 */
interface ECMultiplier
{
    /**
     * Multiplies the <code>ECPoint p</code> by <code>k</code>, i.e.
     * <code>p</code> is added <code>k</code> times to itself.
     * @param p The <code>ECPoint</code> to be multiplied.
     * @param k The factor by which <code>p</code> i multiplied.
     * @return <code>p</code> multiplied by <code>k</code>.
     */
    ECPoint multiply(ECPoint p, BigInteger k, PreCompInfo preCompInfo);
}
