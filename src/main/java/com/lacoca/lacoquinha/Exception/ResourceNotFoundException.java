package com.lacoca.lacoquinha.Exception;

public class ResourceNotFoundException extends  RuntimeException  {
        public ResourceNotFoundException (String mensagem){
            super(mensagem);
        }
}
