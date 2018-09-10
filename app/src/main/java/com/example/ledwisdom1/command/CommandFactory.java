package com.example.ledwisdom1.command;

public interface CommandFactory {
    OnOffCommand onOffCommand();

    BrightnessCommand brightnessCommand();

    ColorCommand colorCommand();



}
