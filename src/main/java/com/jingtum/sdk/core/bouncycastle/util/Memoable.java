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

package com.jingtum.sdk.core.bouncycastle.util;

public interface Memoable
{
    /**
     * Produce a copy of this object with its configuration and in its current state.
     * The returned object may be used simply to store the state, or may be used as a similar object
     * starting from the copied state.
     */
    public Memoable copy();

    /**
     * Restore a copied object state into this object.
     * Implementations of this method <em>should</em> try to avoid or minimise memory allocation to perform the reset.
     *
     * @param other an object originally {@link #copy() copied} from an object of the same type as this instance.
     */
    public void reset(Memoable other);
}
