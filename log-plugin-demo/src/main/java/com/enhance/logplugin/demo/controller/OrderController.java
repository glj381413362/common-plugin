package com.enhance.logplugin.demo.controller;

import com.enhance.annotations.EnableProfiler;
import com.enhance.annotations.Log;
import com.enhance.annotations.LogProfiler;
import com.enhance.logplugin.demo.controller.dto.OrderDetailDTO;
import com.enhance.logplugin.demo.entity.Order;
import com.enhance.logplugin.demo.service.OrderService;
import com.enhance.logplugin.demo.util.SleepUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *
 * </p>
 *
 * @author gongliangjun 2019/07/01 11:18
 */
@RestController
@RequestMapping(value = "/orders")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderController {

  private final OrderService orderService;



//  @Log(itemIds = "order.orderCode")
  @Log
  @GetMapping()
  public List<Order> queryOrder(Order order) {
    return orderService.listOrder(order);
  }


  @EnableProfiler
  @Log(itemIds = "orderCode")
  @GetMapping("/{orderCode}")
  public OrderDetailDTO queryOrder(@PathVariable("orderCode") String orderCode) {
    SleepUtil.threadSleep(1,5);
    OrderDetailDTO orderDetailDTO = orderService.queryOrderDetail(orderCode);
    return orderDetailDTO;
  }

  @PutMapping()
  public Order updateOrder(@RequestBody @Validated(value = {OrderDetailDTO.Update.class}) OrderDetailDTO orderDetailDTO) {
    return orderService.update(orderDetailDTO);
  }

}
