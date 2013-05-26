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

        void init(void);
        void reset(void);

        void readByte(byte address);
        void writeByte(byte address, byte destination);

        // Total System RAM (MAX 64kB)
        byte systemRAM[65535];

        ADDRESS_MODE mode;
};

#endif // MEMORY_H_INCLUDED
