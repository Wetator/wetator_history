/*
 * Copyright (c) 2008-2010 Ronald Brill
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.rbri.wet.util;

import java.util.regex.MatchResult;

import dk.brics.automaton.RunAutomaton;


/**
 *
 * @author rbri
 */
public final class AutomatonShortMatcher implements MatchResult {

    public AutomatonShortMatcher(final CharSequence chars, final RunAutomaton automaton) {
        this.chars = chars;
        this.automaton = automaton;
    }

    private final RunAutomaton automaton;
    private final CharSequence chars;

    private int matchStart = -1;
    private int matchEnd = -1;

    /**
     * Find the next matching subsequence of the input.
     * <br />
     * This also updates the values for the {@code start}, {@code end}, and
     * {@code group} methods.
     *
     * @return {@code true} if there is a matching subsequence.
     */
    public boolean find() {
        int begin;
        if (matchStart == -2) {
            return false;
        } else if (matchStart == -1) {
            begin = 0;
        } else {
            begin = matchStart + 1;
        }

        int match_start;
        int match_end;
        if (automaton.isAccept(automaton.getInitialState())) {
            match_start = begin;
            match_end = begin;
        } else {
            match_start = -1;
            match_end = -1;
        }
        int l = chars.length();
        while (begin < l) {
            int p = automaton.getInitialState();
            for (int i = begin; i < l; i += 1) {
                final int new_state = automaton.step(p, chars.charAt(i));
                if (new_state == -1) {
                    break;
                } else if (automaton.isAccept(new_state)) {
                    if (match_start == -1) {
                        match_start = begin;
                    }
                    match_end = i;

                    setMatch(match_start, match_end + 1);
                    return true;
                }
                p = new_state;
            }
            if (match_start != -1) {
                setMatch(match_start, match_end + 1);
                return true;
            }
            begin += 1;
        }
        if (match_start != -1) {
            setMatch(match_start, match_end + 1);
            return true;
        } else {
            setMatch(-2, -2);
            return false;
        }
    }

    private void setMatch(final int matchStart, final int matchEnd) throws IllegalArgumentException {
        if (matchStart > matchEnd) {
            throw new IllegalArgumentException("Start must be less than or equal to end: " + matchStart + ", " + matchEnd);
        }
        this.matchStart = matchStart;
        this.matchEnd = matchEnd;
    }

    /**
     * Returns the offset after the last character matched.
     *
     * @return The offset after the last character matched.
     * @throws IllegalStateException if there has not been a match attempt or
     *  if the last attempt yielded no results.
     */
    public int end() throws IllegalStateException {
        matchGood();
        return matchEnd;
    }

    /**
     * Returns the offset after the last character matched of the specified
     * capturing group.
     * <br />
     * Note that because the automaton does not support capturing groups the
     * only valid group is 0 (the entire match).
     *
     * @param group the desired capturing group.
     * @return The offset after the last character matched of the specified
     *  capturing group.
     * @throws IllegalStateException if there has not been a match attempt or
     *  if the last attempt yielded no results.
     * @throws IndexOutOfBoundsException if the specified capturing group does
     *  not exist in the underlying automaton.
     */
    public int end(final int group) throws IndexOutOfBoundsException, IllegalStateException {
        onlyZero(group);
        return end();
    }

    /**
     * Returns the subsequence of the input found by the previous match.
     *
     * @return The subsequence of the input found by the previous match.
     * @throws IllegalStateException if there has not been a match attempt or
     *  if the last attempt yielded no results.
     */
    public String group() throws IllegalStateException {
        matchGood();
        return chars.subSequence(matchStart, matchEnd).toString();
    }

    /**
     * Returns the subsequence of the input found by the specified capturing
     * group during the previous match operation.
     * <br />
     * Note that because the automaton does not support capturing groups the
     * only valid group is 0 (the entire match).
     *
     * @param group the desired capturing group.
     * @return The subsequence of the input found by the specified capturing
     *  group during the previous match operation the previous match. Or
     *  {@code null} if the given group did match.
     * @throws IllegalStateException if there has not been a match attempt or
     *  if the last attempt yielded no results.
     * @throws IndexOutOfBoundsException if the specified capturing group does
     *  not exist in the underlying automaton.
     */
    public String group(final int group) throws IndexOutOfBoundsException, IllegalStateException {
        onlyZero(group);
        return group();
    }

    /**
     * Returns the number of capturing groups in the underlying automaton.
     * <br />
     * Note that because the automaton does not support capturing groups this
     * method will always return 0.
     *
     * @return The number of capturing groups in the underlying automaton.
     */
    public int groupCount() {
        return 0;
    }

    /**
     * Returns the offset of the first character matched.
     *
     * @return The offset of the first character matched.
     * @throws IllegalStateException if there has not been a match attempt or
     *  if the last attempt yielded no results.
     */
    public int start() throws IllegalStateException {
        matchGood();
        return matchStart;
    }

    /**
     * Returns the offset of the first character matched of the specified
     * capturing group.
     * <br />
     * Note that because the automaton does not support capturing groups the
     * only valid group is 0 (the entire match).
     *
     * @param group the desired capturing group.
     * @return The offset of the first character matched of the specified
     *  capturing group.
     * @throws IllegalStateException if there has not been a match attempt or
     *  if the last attempt yielded no results.
     * @throws IndexOutOfBoundsException if the specified capturing group does
     *  not exist in the underlying automaton.
     */
    public int start(int group) throws IndexOutOfBoundsException, IllegalStateException {
        onlyZero(group);
        return start();
    }

    /** Helper method that requires the group argument to be 0. */
    private static void onlyZero(final int group) throws IndexOutOfBoundsException {
        if (group != 0) {
            throw new IndexOutOfBoundsException("The only group supported is 0.");
        }
    }

    /** Helper method to check that the last match attempt was valid. */
    private void matchGood() throws IllegalStateException {
        if ((matchStart < 0) || (matchEnd < 0)) {
            throw new IllegalStateException("There was no available match.");
        }
    }
}
