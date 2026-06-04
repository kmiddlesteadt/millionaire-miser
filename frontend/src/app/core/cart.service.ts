import { Injectable, computed, signal } from '@angular/core';
import { CartItem, Product } from './api.models';

@Injectable({ providedIn: 'root' })
export class CartService {
  private readonly itemsState = signal<CartItem[]>([]);

  readonly items = this.itemsState.asReadonly();
  readonly count = computed(() => this.itemsState().reduce((sum, item) => sum + item.quantity, 0));
  readonly total = computed(() => this.itemsState().reduce((sum, item) => sum + item.product.price * item.quantity, 0));

  add(product: Product): void {
    const existing = this.itemsState().find((item) => item.product.id === product.id);
    if (existing) {
      this.changeQuantity(product.id, existing.quantity + 1);
      return;
    }
    this.itemsState.update((items) => [...items, { product, quantity: 1 }]);
  }

  changeQuantity(productId: number, quantity: number): void {
    if (quantity <= 0) {
      this.remove(productId);
      return;
    }
    this.itemsState.update((items) =>
      items.map((item) => (item.product.id === productId ? { ...item, quantity } : item))
    );
  }

  remove(productId: number): void {
    this.itemsState.update((items) => items.filter((item) => item.product.id !== productId));
  }

  clear(): void {
    this.itemsState.set([]);
  }
}
