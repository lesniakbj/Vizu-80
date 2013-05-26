#include "Memory.h"

Memory::Memory()
{
    printf("Initializing Memory Bank...");
}

Memory::~Memory()
{
    printf("Destroying Memory Bank...");
}

void Memory::init()
{
    for(int i = 0; i < 65535; i++)
        systemRAM[i] = 0x00;
}
