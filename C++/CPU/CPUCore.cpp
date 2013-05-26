#include "CPUCore.h"

CPUCore::CPUCore(void)
{
    printf("Initializing CPU-Core...\n");
}

CPUCore::~CPUCore(void)
{
    printf("Destroying CPU-Core\n");
}


void CPUCore::init(void)
{
    flags = 0x0034; // (11010000)
    A, iX, iY = 0;

    stackPointer = 0x00FD;
}

void CPUCore::reset(void)
{
    stackPointer -= 0x03;
    flags = flags | 0x04;
}
