#ifndef CPU_H_INCLUDED
#define CPU_H_INCLUDED

#include "Typedefs.h"

class CPU
{
    public:
        CPU(void);
        ~CPU(void);

    private:

        /*
        / ========================
        /      BEGIN REGISTERS
        / ========================
        */

        // 8 Bit Registers
        Byte A;
        Byte B;
        Byte C;
        Byte D;
        Byte E;
        Byte H;
        Byte L;

        // 16 Bit Registers
        Two_Bytes programCounter;
        Two_Bytes stackPointer;

        // Flags Register(s)
        bool zeroFlag;
        bool subtractionFlag;
        bool halfCarryFlag;
        bool carryFlag;

        // Instruction Timing Registers
        Byte instructMClock;
        Byte instructTClock;

        /*
        / ========================
        /     BEGIN TIMERS
        / ========================
        */

        // Master CPU Timers
        Byte mClock;
        Byte tClock;

};
#endif // CPU_H_INCLUDED
