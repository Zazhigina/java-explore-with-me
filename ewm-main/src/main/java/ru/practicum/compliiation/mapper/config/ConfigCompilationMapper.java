package ru.practicum.compliiation.mapper.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration

public class ConfigCompilationMapper {
    @Bean
    public ModelMapper moduleMapper() {
        ModelMapper mapper = new ModelMapper();
        return mapper;
    }
}
