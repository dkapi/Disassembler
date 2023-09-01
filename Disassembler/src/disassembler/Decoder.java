
//Team memebers: Dino Kapic, Elijah Brady
// Com Sci 321



package disassembler;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Decoder {
    public byte[] byteArr  = new byte[4];
    private final InputStream is;

    private static final Map<Integer, String[]> opMap;
    private static final Map<Integer, String> condMap;

    static {
        opMap = new HashMap<Integer, String[]>();
        opMap.put(1112, new String[]{"R", "ADD"});
        opMap.put(580, new String[]{"I","ADDI"});
        opMap.put(708, new String[]{"I","ADDIS"});
        opMap.put(1104, new String[]{"R", "AND"});
        opMap.put(584,new String[]{"I", "ADDI"});
        opMap.put(5,new String[]{"B", "B"});
        opMap.put(84,new String[]{"CB","B."});
        opMap.put(37,new String[]{"B","BL"});
        opMap.put(1712,new String[]{"R","BR"});
        opMap.put(181,new String[]{"CB","CBNZ"});
        opMap.put(180,new String[]{"CB","CBZ"});
        opMap.put(1616,new String[]{"R","EOR"});
        opMap.put(1679,new String[]{"I", "EORI"});
        opMap.put(1986,new String[]{"D","LDUR"});
        opMap.put(1691,new String[]{"R","LSL"});
        opMap.put(1690,new String[]{"R","LSR"});
        opMap.put(1360,new String[]{"R","ORR"});
        opMap.put(712,new String[]{"I","ORRI"});
        opMap.put(1983,new String[]{"D", "STUR"});
        opMap.put(1624,new String[]{"R","SUB"});
        opMap.put(836,new String[]{"I","SUBI"});
        opMap.put(964,new String[]{"I", "SUBIS"});
        opMap.put(1880,new String[]{"R", "SUBS"});
        opMap.put(1240,new String[]{"R","MUL"});
        opMap.put(2037,new String[]{"R","PRNT"});
        opMap.put(2036,new String[]{"R","PRNL"});
        opMap.put(2038,new String[]{"R","DUMP"});
        opMap.put(2039,new String[]{"R","HALT"});

        condMap = new HashMap<Integer,String>();
        condMap.put(0, "EQ");
        condMap.put(1, "NE");
        condMap.put(2,"HS");
        condMap.put(3,"LO");
        condMap.put(4,"MI");
        condMap.put(5,"PL");
        condMap.put(6,"VS");
        condMap.put(7,"VC");
        condMap.put(8,"HI");
        condMap.put(9,"LS");
        condMap.put(10,"GE");
        condMap.put(11,"LT");
        condMap.put(12,"GT");
        condMap.put(13,"LE");
    }



    public static void main(String[] args) throws IOException {

        File file = new File(args[0]);
        final InputStream is = new DataInputStream(new FileInputStream(file));
        Decoder decoder = new Decoder(is);
        
        //main loop to iterate data and write to new file
        while(decoder.getNextInst()){

            int instruc = decoder.byteToInt();

            String[] decodeType = new String[2];
            for(int i= 21; i< 32; ++i){
                int opCode = instruc >>> i;
                if(opMap.get(opCode) != null){
                    decodeType = opMap.get(opCode);
                }
            }

            switch(decodeType[0]){
                case "R":
                    decoder.decodeR(decodeType[1]);
                    break;
                case "I":
                    decoder.decodeI(decodeType[1]);
                    break;
                case "D":
                    decoder.decodeD(decodeType[1]);
                    break;
                case "CB":
                    decoder.decodeCB(decodeType[1]);
                    break;
                case "B":
                    decoder.decodeB(decodeType[1]);
                    break;
                default:
                    System.out.println("unknown instruction");
            }
        }
    }

    public Decoder(InputStream is){
        this.is = is;
    }
    
    public boolean getNextInst() throws IOException {
        return is.read(byteArr,0,4) >=0;
    }

    public int byteToInt(){
        int b1 = (int)byteArr[3] << 0;
        int b2 = (int)byteArr[2] << 8;
        int b3 = (int)byteArr[1] << 16;
        int b4 = (int)byteArr[0] << 24;

        return b4 + b3 + b2 + b1;
    }

    //below are decode functions for each instruction type
    public void decodeR(String type) {
        int instruct = byteToInt();
        int RM = (instruct >>> 16) & 15;
        int RN = (instruct >>> 5) & 15;
        int RD = instruct & 15;
        int SHAMT = (instruct >>> 10) & 31;
        System.out.print(type + " ");
        if(type.equals("LSL")  || type.equals("LSR")){
            System.out.print("X"+ RD + " " + "X" + RN + " " + "#" + SHAMT + "\n");
        }else if(type.equals("BR")){
            System.out.print(type + " " + RN + "\n");
        } else if(type.equals("DUMP") || type.equals("PRNT") || type.equals("PRNL") || type.equals("HALT")){
            System.out.print("\n");
        }
        else {
            System.out.print("X" + RD + " " + "X" + RN + " " + "X" + RM+"\n");
        }
    }

    public void decodeI(String type) {
        int instruct = byteToInt();
        int ALU = (instruct >>> 10)& 2047;
        int RN = (instruct >>>5) & 15;
        int RD = instruct & 15;

//        System.out.println("tthis is that yes: " + (4608 >>> 10));
//        System.out.println(Integer.toBinaryString(instruct));
//        System.out.println("this is instruct: " + Integer.toBinaryString((instruct >>> 9) & 2047));
//        System.out.println("RN is in binary:" + Integer.toBinaryString((instruct >>> 5) & 15));
        System.out.print(type + " ");
        System.out.print("X" + RD + " " + "X" + RN + " " + "#" + ALU + "\n");

    }

    public void decodeD(String type) {
        int instruct = byteToInt();
        int DT = (instruct >>> 12) & 2047;
        int RN = (instruct >>> 5) & 15;
        int RT = instruct & 15;
        System.out.print(type + " ");
        System.out.print("X" + RT + " [" + "X" + RN + " " + "#" + DT + "]" +"\n");
    }

    public void decodeB(String type) {
        int instruct = byteToInt();
        System.out.println("byte to binart " + Integer.toBinaryString(byteToInt()));
        int condB = (instruct >>> 26) & 67108863;
        System.out.print(type + " ");
        System.out.print(condB + "\n");
    }

    public void decodeCB(String type) {
        int instruct = byteToInt();
        int BR = (instruct >>> 5) & 15;
        int RT = instruct & 15;
        String cond = condMap.get(RT);
        System.out.println("this is rt: "  +RT);
        System.out.print(type + "." + cond + " " + BR + "\n");

    }
}
