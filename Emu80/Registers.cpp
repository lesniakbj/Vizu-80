#include "Registers.h"

Registers::Registers()
{
    // Initialize Registers, Extended Registers, and Flags
    initRegs();
    initExtendRegs();
    initFlags();
}

void Registers::initRegs()
{
    this->A = 0x00;
    this->B = 0x00;
    this->C = 0x00;
    this->D = 0x00;
    this->E = 0x00;
    this->H = 0x00;
    this->L = 0x00;
}

void Registers::initExtendRegs()
{
    this->ProgramCounter = 0x0000;
    this->StackPointer = 0x0000;
}

void Registers::initFlags()
{
    this->F = false;
}
