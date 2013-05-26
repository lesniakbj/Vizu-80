#include "Memory.h"

Memory::Memory(void)
{
    printf("Initializing Memory Bank...");
}

Memory::~Memory(void)
{
    printf("Destroying Memory Bank...");
}

void Memory::init(void)
{
    // Initialize Main Memory Banks
    for(int i = 0; i < 65535; i++)
        systemRAM[i] = 0xFF;

    // Initialize Specific Memory Locations
    systemRAM[0x0008] = 0xF7;
    systemRAM[0x0009] = 0xEF;
    systemRAM[0x000A] = 0xDF;
    systemRAM[0x000F] = 0xBF;

    // Initialize Audio & IO Locations
    systemRAM[0x4017] = 0x00;
    systemRAM[0x4015] = 0x00;

    for(int n = 0; n <= 0x000F; n++)
        systemRAM[0x4000 + n] = 0x00;

}

void Memory::reset(void)
{
    systemRAM[0x4015] = 0x00;
}
