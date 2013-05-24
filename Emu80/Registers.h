#ifndef REGISTERS_H_INCLUDED
#define REGISTERS_H_INCLUDED

#include "Typedefs.h"

class Registers
{
    public:
        Registers();
        ~Registers();

        // 8 Bit Registers
        Byte A;
        Byte B;
        Byte C;
        Byte D;
        Byte E;
        Byte H;
        Byte L;

        // 16 Bit Registers
        Two_Bytes ProgramCounter;
        Two_Bytes StackPointer;

        // Flag Register
        bool F;

    private:
        void initRegs();
        void initExtendRegs();
        void initFlags();
};

#endif // REGISTERS_H_INCLUDED
