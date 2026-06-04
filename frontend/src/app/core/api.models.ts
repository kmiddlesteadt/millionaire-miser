export interface UserSummary {
  id: number;
  fullName: string;
  email: string;
  roles: Array<'USER' | 'ADMIN'>;
}

export interface AuthResponse {
  token: string;
  user: UserSummary;
}

export interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  stockQuantity: number;
  active: boolean;
  createdAt: string;
}

export interface ProductRequest {
  name: string;
  description: string;
  price: number;
  stockQuantity: number;
  active?: boolean;
}

export interface CartItem {
  product: Product;
  quantity: number;
}

export interface OrderLineRequest {
  productId: number;
  quantity: number;
}

export interface CreateOrderRequest {
  items: OrderLineRequest[];
}

export type OrderStatus = 'PENDING' | 'PAID' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED';

export interface OrderItem {
  productId: number;
  productName: string;
  unitPrice: number;
  quantity: number;
  lineTotal: number;
}

export interface Order {
  id: number;
  userId: number;
  customerEmail: string;
  items: OrderItem[];
  total: number;
  status: OrderStatus;
  createdAt: string;
}
