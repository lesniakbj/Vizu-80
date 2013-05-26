#include <fstream>

#include "CPU\CPUCore.h"
#include "Memory\Memory.h"

// Type Defenitions
typedef unsigned char byte;
typedef unsigned short two_bytes;

// NES Components
CPUCore *cpu;
Memory *mem;

// Function Prototypes
void initializeComponents();

int main()
{
    printf("Welcome to NES Emu\n");
    printf("Made By: Brendan Lesniak\n");

    initializeComponents();

    return 0;
}


void initializeComponents()
{
    cpu = new CPUCore;
    mem = new Memory;

    cpu->init();
    mem->init();
}
