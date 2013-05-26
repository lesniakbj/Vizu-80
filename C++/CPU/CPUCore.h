#ifndef CPUCORE_H_INCLUDED
#define CPUCORE_H_INCLUDED

#include <fstream>

// Type Defenitions
typedef unsigned char byte;
typedef unsigned short two_bytes;

class CPUCore
{
    public:
        CPUCore(void);
        ~CPUCore(void);

        void init();
        void reset();

    private:

        void decodeOpCode(void);

        // 8-Bit Registers (Accumulator & Index Registers, Stack Pointer)
        byte A;
        byte iX;
        byte iY;
        byte stackPointer;

        // 16-Bit Register (Program Counter)
        two_bytes programCounter;

        // Flag Register
        // Sign  |  Over |  None  |SoftInt|  Dec  | Ints  |  Zero  |  Carry
        //   0   |   0   |   0    |   0   |   0   |   0   |    0   |    0
        byte flags;
};

#endif // CPUCORE_H_INCLUDED
