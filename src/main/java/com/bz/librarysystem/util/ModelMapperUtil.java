package com.bz.librarysystem.util;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperUtil {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Basic configuration for automatic mapping
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STANDARD) // Less strict than STRICT
                .setFieldMatchingEnabled(true);

        return modelMapper;
    }




}