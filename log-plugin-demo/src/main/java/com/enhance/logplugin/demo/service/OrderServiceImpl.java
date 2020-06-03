package com.enhance.logplugin.demo.service;

import com.common.tools.util.BeanUtil;
import com.common.tools.util.ListUtil;
import com.common.tools.util.exception.CommonException;
import com.common.tools.util.pojo.Msg;
import com.enhance.annotations.Log;
import com.enhance.logplugin.demo.controller.dto.OrderDetailDTO;
import com.enhance.logplugin.demo.controller.dto.OrderDetailDTO.OrderEntryDetail;
import com.enhance.logplugin.demo.controller.dto.OrderDetailDTO.UserDTO;
import com.enhance.logplugin.demo.dao.OrderEntryMapper;
import com.enhance.logplugin.demo.dao.OrderMapper;
import com.enhance.logplugin.demo.dao.SkuMapper;
import com.enhance.logplugin.demo.dao.UserMapper;
import com.enhance.logplugin.demo.entity.Order;
import com.enhance.logplugin.demo.entity.OrderEntry;
import com.enhance.logplugin.demo.entity.Sku;
import com.enhance.logplugin.demo.entity.User;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers;
import org.springframework.stereotype.Service;

/**
 * <p>
 *
 * </p>
 *
 * @author gongliangjun 2019/07/01 11:18
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@XSlf4j
public class OrderServiceImpl {

  private final UserMapper userMapper;
  private final OrderMapper orderMapper;
  private final OrderEntryMapper orderEntryMapper;
  private final SkuMapper skuMapper;


  /**
   * 列表查询
   *
   * @return java.util.List<com.enhance.logplugin.demo.entity.Order>
   * @author gongliangjun 2020-06-02 4:09 PM
   */
  public List<Order> listOrder(Order order) {
    List<Order> orders = orderMapper.findAll(Example.of(order));
    return orders;
  }


  /**
   * 根据订单编码查询订单详情
   *
   * @return com.enhance.logplugin.demo.controller.dto.OrderDetailDTO
   * @author gongliangjun 2020-06-02 4:10 PM
   */
  public OrderDetailDTO queryOrderDetail(String orderCode) {
    Order order1 = new Order() ;
    order1.setOrderCode(orderCode);
    Optional<Order> orderOptional = orderMapper.findOne(Example.of(order1));
    OrderDetailDTO detailDTO = new OrderDetailDTO();
    if (orderOptional.isPresent()) {
      Order order = orderOptional.get();
      //===============================================================================
      //  组装订单dto
      //===============================================================================
      conversionOrder(order, detailDTO);
      //===============================================================================
      //  组装订单行dto
      //===============================================================================
      try {
        conversionOrderEntry(order, detailDTO);
      } catch (Exception e) {
        log.throwing(e);
      }
      //===============================================================================
      //  组装用户
      //===============================================================================
      conversionUser(order.getUserId(),detailDTO);

    }
    return detailDTO;
  }

  private void conversionUser(Long userId, OrderDetailDTO detailDTO) {
    Optional<User> byId = userMapper.findById(userId);
    byId.ifPresent(user -> {
      UserDTO userDTO = new UserDTO();
      detailDTO.setUser(userDTO);
      BeanUtil.copySourceToTarget(user,userDTO);
    });
  }

  @Transactional
  @Log(itemIds = {"orderDetailDTO.orderCode", "orderDetailDTO.user.userCode"})
  public Order update(OrderDetailDTO orderDetailDTO) {
    List<OrderEntryDetail> orderEntryDetails = orderDetailDTO.getOrderEntryDetails();
    handOrderEntry(orderEntryDetails);
    UserDTO user = orderDetailDTO.getUser();
    Optional<User> userOptional = userMapper.findById(user.getUserId());
    userOptional.ifPresent(user1 -> {
      BeanUtil.copySourceToTarget(user, user1);
      userMapper.saveAndFlush(user1);
    });
    Optional<Order> orderOptional = orderMapper.findById(orderDetailDTO.getOrderId());
    Order order = orderOptional.orElseThrow(() -> new CommonException(new Msg("根据订单id[{}],未查询到相应订单"),orderDetailDTO.getOrderId()));
    BeanUtil.copySourceToTarget(orderDetailDTO, order);
    order = orderMapper.saveAndFlush(order);
    return order;
  }

  @Log(itemIds = {"orderEntryDetails[0].orderId"})
  public void handOrderEntry(List<OrderEntryDetail> orderEntryDetails) {
    for (OrderEntryDetail orderEntryDetail : orderEntryDetails) {
      Optional<OrderEntry> byId = orderEntryMapper.findById(orderEntryDetail.getOrderEntryId());
      byId.ifPresent(orderEntry -> {
        BeanUtil.copySourceToTarget(orderEntryDetail, orderEntry);
        orderEntryMapper.saveAndFlush(orderEntry);
      });
      OrderDetailDTO.Sku sku = orderEntryDetail.getSku();
      log.info("开始修改sku相关信息");
      Optional<Sku> optionalSku = skuMapper.findById(orderEntryDetail.getSkuId());
      optionalSku.ifPresent(sku1 -> {
        BeanUtil.copySourceToTarget(sku, sku1);
        skuMapper.saveAndFlush(sku1);
      });
    }
  }

  public void conversionOrder(Order order, OrderDetailDTO detailDTO) {
    BeanUtil.copySourceToTarget(order, detailDTO);
  }

  public void conversionOrderEntry(Order order, OrderDetailDTO detailDTO) {
    OrderEntry query = new OrderEntry();
    query.setOrderId(order.getOrderId());
    List<OrderEntry> orderEntries = orderEntryMapper.findAll(Example.of(query));
    if (ListUtil.listIsNotEmpty(orderEntries, "不为空")) {
      ArrayList<OrderEntryDetail> orderEntryArrayList = Lists
          .newArrayListWithCapacity(orderEntries.size());
      for (OrderEntry orderEntry : orderEntries) {
        OrderEntryDetail orderEntryDetail = new OrderEntryDetail();
        BeanUtil.copySourceToTarget(orderEntry, orderEntryDetail);
        orderEntryArrayList.add(orderEntryDetail);
        //===============================================================================
        //  组装商品dto
        //===============================================================================
        OrderDetailDTO.Sku sku = new OrderDetailDTO.Sku();
        orderEntryDetail.setSku(sku);
        try {
          conversionSku(orderEntry.getSkuId(), sku);
        } catch (Exception e) {
          log.catching(e);
        }
      }
      detailDTO.setOrderEntryDetails(orderEntryArrayList);
    } else {
      throw new CommonException(new Msg("根据orderCode:{}未查询到订单行数据"), order.getOrderCode());
    }
  }

  public void conversionSku(Long skuId, OrderDetailDTO.Sku skuDto) {
    Optional<Sku> skuOptional = skuMapper.findById(skuId);
    Sku sku = skuOptional
        .orElseThrow(() -> new CommonException(new Msg("根据skuId:{}未查询到商品数据"), skuId));
    BeanUtil.copySourceToTarget(sku, skuDto);
  }

}
