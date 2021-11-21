package me.FurH.SkyShield.Constants;

/*
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class ShieldConstants {

    public static final int payload_handshake = 1; // agent handshake and pid
    public static final int payload_filelist = 2; // requested filelist
    public static final int payload_askhash = 3; // requested file hash
    public static final int payload_fileinfo = 4; // send file info
    public static final int payload_askid = 5;
    public static final int payload_takeshot = 6;
    public static final int payload_processlist = 7;
    public static final int payload_modulelist = 8;
    public static final int payload_askfilelist = 9;
    public static final int payload_askziphash = 10;
    public static final int payload_ziphash = 11;

    public static final int cmd_protocol = 1; // agent protocol data to inject
    public static final int cmd_error = 2; // error
    public static final int cmd_ready = 3; // agent injected and ready
    public static final int cmd_filetransport = 4; // agent file list data
    public static final int cmd_shoterror = 5; // agent error on screenshot
    public static final int cmd_shotdata = 6; // agent screenshot data
    public static final int cmd_agentpath = 7; // agent path data
    public static final int cmd_sendproceslist = 8;
    public static final int cmd_sendmodulelist = 9;
    public static final int cmd_sendfilelist = 10;
    public static final int cmd_postloaded = 11;

    public static final int error_noprotocol = 1; // agent no compatible protocol
    public static final int error_failedinject = 2; // agent failed to inject
    public static final int error_channellost = 3; // agent channel connection lost on inject
    public static final int error_disconnected = 4; // agent channel disconnected
    public static final int error_payloadread = 5; // agent error on payload read
    public static final int error_errfilelist = 6; // agent error on file list send
    public static final int error_invalidcmd = 7; // agent error unkown command
   
    public static final int serror_ignore = 0;
    
    public static final int serror_disconnected = 7;
    public static final int serror_restarted = 8;
    public static final int serror_namesmismatch = 9;
    public static final int serror_unsuportedprotocol = 10;
    public static final int serror_parsecmdline = 11;
    public static final int serror_wrongattachtime = 12;
    public static final int serror_payloadwronguuid = 13;
    public static final int serror_brokenpayload = 14;
    public static final int serror_filelistwrongtime = 15;
    public static final int serror_wrongjavadir = 16;
    public static final int serror_unsuportedver = 17;
    public static final int serror_unsuportedrev = 18;
    public static final int serror_invalidfile1 = 19;
    public static final int serror_fileencodeerr = 20;
    public static final int serror_filehashwrongtime = 21;
    public static final int serror_wrongfilehash = 22;
    public static final int serror_errordecodehash = 23;
    public static final int serror_attachwrongtime = 24;
    public static final int serror_invalidcryptpid = 25;
    public static final int serror_faileddecodehandshake = 26;
    public static final int serror_failedwritepayload = 27;
    
    public static final int error_filelistwrongtime = 28;
    public static final int error_failedtoinstalldll = 29;
    public static final int error_errortoinstalldll = 30;
    public static final int error_attachreadywrongtime = 31;
    public static final int error_failedwriteprotocol = 32;
    public static final int error_protocolwrongtime = 33;
    public static final int error_attacherror = 34;
    public static final int error_localservererror = 35;
    public static final int error_attachrequestwrongtime = 36;
    public static final int error_nativecmderror = 37;
    public static final int error_attachtimeout = 38;
    
    public static final int error_multipleagents = 39;
    public static final int error_noagentsdetected = 40;
    public static final int error_unkownfiledetected = 41;
    public static final int error_unrequestedfile = 42;
    public static final int error_wrongfilesequence = 43;
    public static final int error_filecomputedalready = 44;
    
    public static final int error_certificatewrongtime = 45;
    public static final int error_wrongagentcertificate = 46;
    public static final int error_wrongjavacertificate = 47;
    
    public static final int error_wronghashpid = 48;
    public static final int error_clientisbusy = 49;
    public static final int error_pidoff = 50;
    public static final int error_userquitserver = 51;
    public static final int error_clientterminated = 52;
    
    public static final int error_hashfiled = 53;
        
}