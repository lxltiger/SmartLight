package com.example.ledwisdom1.command;

public class TelinkCommandFactory implements CommandFactory {
    private int addr;

    public TelinkCommandFactory(int addr) {
        this.addr = addr;
    }

    @Override
    public OnOffCommand onOffCommand() {
        return new TelinkOnOffCommand(addr);
    }

    @Override
    public BrightnessCommand brightnessCommand() {
        return new TelinkBrightnessCommand(addr);
    }

    @Override
    public ColorCommand colorCommand() {
        return new TelinkColorCommand(addr);
    }

}
