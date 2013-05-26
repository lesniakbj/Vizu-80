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
void initializeComponents(void);
void emuStart(void);

int main(void)
{
    printf("Welcome to BreNES Emulator!\n");
    printf("Made By: Brendan Lesniak\n\n");

    // Initialize Graphics & Rendering Environment

    // Initialize User Input

    initializeComponents();

    emuStart();

    return 0;
}


void initializeComponents(void)
{
    cpu = new CPUCore;
    mem = new Memory;

    cpu->init();
    mem->init();
}


void emuStart(void)
{
    // Load the ROM into the CPU

    // MAIN EMULATION LOOP
    while(true)
    {
        // Emulate One Cycle

        // Draw Results of Operations to the Screen

        // Update Key Presses, Store in Memory
    }
}
