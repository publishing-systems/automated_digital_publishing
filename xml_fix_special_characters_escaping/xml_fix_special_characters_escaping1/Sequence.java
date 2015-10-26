/* Copyright (C) 2015  Stephan Kreutzer
 *
 * This file is part of xml_fix_special_characters_escaping1.
 *
 * xml_fix_special_characters_escaping1 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * xml_fix_special_characters_escaping1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with xml_fix_special_characters_escaping1. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/Sequence.java
 * @brief Represents a sequence of characters.
 * @author Stephan Kreutzer
 * @since 2015-10-26
 */



class Sequence
{
    public Sequence(char[] sequence, boolean skip)
    {
        this.sequence = sequence;
        this.skip = skip;
        this.matchCount = 0;
        this.matched = false;
    }

    public boolean GetSkip()
    {
       return this.skip;
    }

    public int CheckChar(char character)
    {
        if (this.matched == false &&
            this.matchCount < this.sequence.length)
        {
            if (this.sequence[this.matchCount] == character)
            {
                this.matchCount += 1;

                if (this.matchCount == this.sequence.length)
                {
                    this.matched = true;
                    return 2;
                }

                return 1;
            }
            else
            {
                Reset();
                return 0;
            }
        }

        return 2;
    }

    public boolean GetMatching()
    {
        return this.matchCount > 0;
    }

    public boolean GetMatched()
    {
        return this.matched;
    }

    public void Reset()
    {
        this.matchCount = 0;
        this.matched = false;
    }

    public char[] GetSequence()
    {
        char[] sequenceCopy = new char[sequence.length];
        System.arraycopy(this.sequence, 0, sequenceCopy, 0, sequence.length);
        return sequenceCopy;
    }

    public int GetLength()
    {
        return this.sequence.length;
    }

    protected char[] sequence;
    protected boolean skip;
    protected int matchCount;
    protected boolean matched;
}
