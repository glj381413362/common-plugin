package com.enhance.logplugin.demo.service;

import com.enhance.annotations.EnableProfiler;
import com.enhance.annotations.Log;
import com.enhance.annotations.LogProfiler;
import com.enhance.logplugin.demo.controller.dto.OrderDetailDTO;
import com.enhance.logplugin.demo.controller.dto.OrderDetailDTO.OrderEntryDetail;
import com.enhance.logplugin.demo.entity.Order;
import java.util.List;
import javax.transaction.Transactional;

/**
 * <p>
 *
 * </p>
 *
 * @author gongliangjun 2019/07/01 11:18
 */
public interface OrderService {

  List<Order> listOrder(Order order);

  OrderDetailDTO queryOrderDetail(String orderCode);

  Order update(OrderDetailDTO orderDetailDTO);

  void handOrderEntry(List<OrderEntryDetail> orderEntryDetails);

  void conversionOrder(Order order, OrderDetailDTO detailDTO);
  void tempA();
  void tempB();

  void conversionOrderEntry(Order order, OrderDetailDTO detailDTO);

  void conversionSku(Long skuId, OrderDetailDTO.Sku skuDto);
}
