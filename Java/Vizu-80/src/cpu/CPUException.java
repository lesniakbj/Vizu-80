package src.cpu;

public class CPUException extends Exception
{
        public final static String INVALID_OP         = "Invalid opcode!";
        public final static String UNIMPLEMENTED_OP   = "Unimplemented opcode!";
        public final static String INVALID_REG        = "Invalid register!";
        public final static String PROCESSOR_HALT     = "CPU Halted!";
        public final static String FETCH_FAILED       = "Opcode Fetch Failed!";

        /**
         * Empty exception for the Z80 emulator
         */
        public CPUException()
        {
                super();
        }

        /**
         * Known exception for the Z80 emulator
         *
         * @param msg
         *            Emulator exception message
         */
        public CPUException(String msg)
        {
                super(msg);
        }
}
