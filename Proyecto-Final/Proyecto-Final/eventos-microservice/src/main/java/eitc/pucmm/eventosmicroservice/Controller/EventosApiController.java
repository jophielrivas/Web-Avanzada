package eitc.pucmm.eventosmicroservice.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTimeComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import eitc.pucmm.eventosmicroservice.Repository.CompraRepository;
import eitc.pucmm.eventosmicroservice.Repository.EventosRepository;
import eitc.pucmm.eventosmicroservice.entidades.Compra;
import eitc.pucmm.eventosmicroservice.entidades.Evento;

@RestController
public class EventosApiController {
    @Autowired
    EventosRepository eventosRepository;

    @Autowired
    CompraRepository compraRepository;

    //Funciona
    @GetMapping("/compras/general/{filter}")
    List<Compra> getCompras(@PathVariable String filter){
        List<Compra> compras = compraRepository.findAll();
        switch (filter) {
            case "hoy":
                return getTodayCompras(compras);
            case "pasadas":
                return getComprasPasadas(compras);          
            default:
                return compras;
        }
    }

    //Funciona
    @GetMapping("/compras/{id}")
    List<Compra> getAllComprasById(@PathVariable String id){
        long aux = Long.parseLong(id);
        return compraRepository.findAllByIdUsuario(aux);
    }

    @PostMapping("/compra/{id}")
    void realizaCompra(@RequestBody String body,@PathVariable String id) throws IOException{
        
        Compra compra = new Compra();
        compra.setIdUsuario(Long.parseLong(id));
        compra.setFecha(new Date());
        compra.setTotal(parseEventos(body, compra.getId()));

        compraRepository.save(compra);

    }

    @GetMapping("/compra/{id}")
    Compra getCompraById(@PathVariable String id){
        long aux = Long.parseLong(id);
        return compraRepository.findById(aux).orElse(null);
    }

    @GetMapping("/compra/eventos/{id}")
    List<Evento> getEventos(@PathVariable String id){
        long idAux = Long.parseLong(id);
        return eventosRepository.findAllByIdCompra(idAux);
    }

    private List<Compra> getTodayCompras(List<Compra> compras) {
        List<Compra> aux = new ArrayList<>();
        Date hoy = new Date();
        DateTimeComparator dateTimeComparator = DateTimeComparator.getDateOnlyInstance();
        for (Compra compra : compras) {
            if(dateTimeComparator.compare(hoy, compra.getFecha()) == 0){
                aux.add(compra);
            }
        }
        return aux;
    }

    private List<Compra> getComprasPasadas(List<Compra> compras) {
        List<Compra> aux = new ArrayList<>();
        Date hoy = new Date();
        DateTimeComparator dateTimeComparator = DateTimeComparator.getDateOnlyInstance();
        for (Compra compra : compras) {
            if( dateTimeComparator.compare(hoy,compra.getFecha()) > 0){
                aux.add(compra);
            }
        }
        return aux;    
    }

    private int parseEventos(String body, long l) {
        System.out.println(body);
        int[] carrito = {0,0,0,0};

        String aux = body.substring(1,body.length()-1);
        List<String> myList = new ArrayList<String>(Arrays.asList(aux.split(",")));
        for (int i = 0; i < 4; i++) {
            carrito[i] = Integer.parseInt(myList.get(i).replaceAll("\\s+",""));
        }

        String[] productos = {"Pre-Boda","Boda","Cumpleaños","Vide de Evento"};
        int[] precio = {1000,5000,3000,4000};
        int total = 0;
        Evento evento;
        for(int i = 0; i < 4; i++){
            if(carrito[i] != 0 ){
                evento = new Evento(productos[i], precio[i], carrito[i]);
                evento.setIdCompra(l);
                eventosRepository.save(evento);
                total += precio[i] * carrito[i];
            }
        }
        return total;
    }
}
