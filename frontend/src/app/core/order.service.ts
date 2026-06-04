import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from './api.config';
import { CreateOrderRequest, Order, OrderStatus } from './api.models';

@Injectable({ providedIn: 'root' })
export class OrderService {
  private readonly http = inject(HttpClient);

  create(request: CreateOrderRequest): Observable<Order> {
    return this.http.post<Order>(`${API_BASE_URL}/orders`, request);
  }

  mine(): Observable<Order[]> {
    return this.http.get<Order[]>(`${API_BASE_URL}/orders/me`);
  }

  all(): Observable<Order[]> {
    return this.http.get<Order[]>(`${API_BASE_URL}/orders`);
  }

  updateStatus(id: number, status: OrderStatus): Observable<Order> {
    return this.http.patch<Order>(`${API_BASE_URL}/orders/${id}/status`, { status });
  }
}
