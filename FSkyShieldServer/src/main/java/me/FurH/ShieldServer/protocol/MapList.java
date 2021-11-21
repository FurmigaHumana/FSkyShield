package me.FurH.ShieldServer.protocol;

import java.util.HashMap;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class MapList {

    private static final HashMap<Integer, MapEntry> maps;

    static {

        maps = new HashMap<>();

        // C17PacketCustomPayload,NetworkManager,Minecraft,GuiConnecting
        register(47,
                "1.8 - 1.8.9",
                new MapList(
                        e("1.8", "mc,gr,bsu,bwb,ji"),
                        e("1.8.1", "md,gr,bss,bvz,jj"),
                        e("1.8.2, 1.8.3", "in,el,avf,axa,gh"),
                        e("1.8.4, 1.8.5, 1.8.6, 1.8.7, 1.8.8, 1.8.9", "im,ek,ave,awz,gg")
                )
        );

        register(107,
                "1.9",
                new MapList(
                        e("1.9", "iq,ek,bcf,bei,gh")
                )
        );

        register(108,
                "1.9.1",
                new MapList(
                        e("1.9.1", "iq,ek,bcc,bef,gh")
                )
        );

        register(109,
                "1.9.2",
                new MapList(
                        e("1.9.2", "iq,ek,bcc,bef,gh")
                )
        );

        register(110,
                "1.9.3, 1.9.4",
                new MapList(
                        e("1.9.3, 1.9.4", "ir,em,bcd,beg,gj")
                )
        );

        register(210,
                "1.10, 1.10.1, 1.10.2",
                new MapList(
                        e("1.10, 1.10.1, 1.10.2", "it,eo,bcx,bfa,gl")
                )
        );

        register(315,
                "1.11",
                new MapList(
                        e("1.11", "iw,er,beq,bgt,go")
                )
        );

        register(316,
                "1.11.1, 1.11.2",
                new MapList(
                        e("1.11.1, 1.11.2", "iw,er,bes,bgv,go")
                )
        );

        register(335,
                "1.12",
                new MapList(
                        e("1.12", "lh,gw,bhz,bkp,iw")
                )
        );

        register(338,
                "1.12.1",
                new MapList(
                        e("1.12.1", "lh,gw,bib,bkr,iw")
                )
        );

        register(340,
                "1.12.2",
                new MapList(
                        e("1.12.2", "lh,gw,bib,bkr,iw")
                )
        );
        
        // 1.13 and up has different payload, check later

        register(393,
                "1.13",
                new MapList(
                        e("1.13", "mp,hw,cfi,ciz,jy,pc")
                )
        );

        register(401,
                "1.13.1",
                new MapList(
                        e("1.13.1", "mp,hw,cfs,cjj,jy,pc")
                )
        );

        register(404,
                "1.13.2",
                new MapList(
                        e("1.13.2", "mp,hw,cft,cjk,jy,pc")
                )
        );

        register(477,
                "1.14",
                new MapList(
                        e("1.14", "od,ja,cvi,cyu,lc,qs")
                )
        );

        register(480,
                "1.14.1",
                new MapList(
                        e("1.14.1", "od,ja,cvk,cyw,lc,qs")
                )
        );

        register(485,
                "1.14.2",
                new MapList(
                        e("1.14.2", "od,ja,cvk,cyw,lc,qs")
                )
        );

        register(490,
                "1.14.3",
                new MapList(
                        e("1.14.3", "oe,jb,cvo,cza,ld,qt")
                )
        );

        register(498,
                "1.14.4",
                new MapList(
                        e("1.14.4", "og,jc,cyc,dbo,lf,qv")
                )
        );

        register(573,
                "1.15",
                new MapList(
                        e("1.15", "px,kt,dbl,dfb,mw,sm")
                )
        );

        register(575,
                "1.15.1",
                new MapList(
                        e("1.15.1", "px,kt,dbl,dfb,mw,sm")
                )
        );

        register(578,
                "1.15.2",
                new MapList(
                        e("1.15.2", "px,kt,dbn,dfd,mw,sm")
                )
        );
    }
    
    public static MapEntry byProtocol(int protocol) {
        return maps.get(protocol);
    }

    private static void register(int protocol, String label, MapList list) {
        maps.put(protocol, new MapEntry(label, list));
    }

    private static ProtocolEntry e(String header, String protocol) {
        return new ProtocolEntry(header, protocol);
    }

    public final ProtocolEntry[] protocol;

    public MapList(ProtocolEntry... protocol) {
        this.protocol = protocol;
    }
}
