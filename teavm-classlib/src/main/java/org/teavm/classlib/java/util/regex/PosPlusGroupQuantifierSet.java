/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

/**
 * @author Nikolay A. Kuznetsov
 */
package org.teavm.classlib.java.util.regex;

/**
 * Possessive + quantifier node over groups.
 *
 * @author Nikolay A. Kuznetsov
 */
class PosPlusGroupQuantifierSet extends GroupQuantifierSet {

    public PosPlusGroupQuantifierSet(AbstractSet innerSet, AbstractSet next,
            int type) {
        super(innerSet, next, type);
        ((JointSet) innerSet).setNext(FSet.posFSet);

    }

    public int matches(int stringIndex, CharSequence testString,
            MatchResultImpl matchResult) {

        int nextIndex;
        if ((nextIndex = innerSet.matches(stringIndex, testString, matchResult)) < 0) {
            return -1;
        } else if (nextIndex > stringIndex) {
            stringIndex = nextIndex;
            while ((nextIndex = innerSet.matches(stringIndex, testString,
                    matchResult)) > stringIndex) {
                stringIndex = nextIndex;
            }
        }

        return next.matches(stringIndex, testString, matchResult);
    }
}
