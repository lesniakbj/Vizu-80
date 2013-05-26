#ifndef MEMORY_H_INCLUDED
#define MEMORY_H_INCLUDED

#include <fstream>

// Type Defenitions
typedef unsigned char byte;
typedef unsigned short two_bytes;

enum ADDRESS_MODE
{
    IMMEDIATE,
    ZERO_PAGE,
    ABSOLUTE,
    INDIRECT
};


class Memory
{
    public:
        Memory(void);
        ~Memory(void);

        void init();

        void readByte(two_bytes address);
        void writeByte(two_bytes address, two_bytes destination);

        // Total System RAM (MAX 64kB)
        byte systemRAM[65535];
};

#endif // MEMORY_H_INCLUDED
